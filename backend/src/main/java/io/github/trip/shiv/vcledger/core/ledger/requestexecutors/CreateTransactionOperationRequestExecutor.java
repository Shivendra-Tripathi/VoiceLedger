package io.github.trip.shiv.vcledger.core.ledger.requestexecutors;

import java.util.List;

import io.github.trip.shiv.vcledger.core.exceptions.custom.internal.CustomerNotFoundException;
import org.springframework.stereotype.Component;

import io.github.trip.shiv.vcledger.core.ledger.operations.CreateTransactionOperation;
import io.github.trip.shiv.vcledger.core.ledger.req.CreateTransactionOperationRequest;
import io.github.trip.shiv.vcledger.core.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.core.ledger.res.CustomerNotFoundFailureResponse;
import io.github.trip.shiv.vcledger.core.ledger.res.CustomerSelectionResponse;
import io.github.trip.shiv.vcledger.core.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.core.ledger.res.TransactionConfirmationResponse;
import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.PersonInfo;
import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.TransactionPreview;
import io.github.trip.shiv.vcledger.core.exceptions.custom.internal.LedgerIntentRequestExecutorMismatchException;
import io.github.trip.shiv.vcledger.core.utilities.SecurityUtils;
import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.PendingOperation;
import io.github.trip.shiv.vcledger.entity.PendingOperation.Status;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.service.CustomerNameMatcherService;
import io.github.trip.shiv.vcledger.service.PendingOperationService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateTransactionOperationRequestExecutor implements LedgerIntentRequestExecutor<CreateTransactionOperationRequest>{

	private final SecurityUtils securityUtils;
	private final PendingOperationService pendingOperationService;
	private final CustomerNameMatcherService customerNameMatcherService;

	
	@Override
	public LedgerResponse execute(LedgerIntentRequest request) {
		
		//Throw if Wrong Request is Passes to this Executor
		if( ! (request instanceof CreateTransactionOperationRequest 
				&& request.getIntentKey().equals(CreateTransactionOperationRequest.intentKey) )) {
			throw new LedgerIntentRequestExecutorMismatchException(
					"The Executor :"+getClass().getName()
			+" can't handle the request with intent :" + request.getIntentKey());
		}
		
		CreateTransactionOperationRequest createRequest = 
				(CreateTransactionOperationRequest)request;
		
		//Load the authenticated User
		User user = securityUtils.getAuthenticatedUser();
		
		//Get the Customers whose name is Similar to the Spoken Name
		List<Customer> customers =
				customerNameMatcherService.getMatchingCustomers(user.getId(),createRequest.getCustomerName());
		
		/*
		 * CASE 1 : When the Customer Name is not found in the database
		 */
		if(customers.isEmpty()) {
			throw new CustomerNotFoundException("Customer not found");
		}
		
		
		
		/*
		 * CASE 2 : When the Customer Name uniquely identifies a Customer Name
		 */
		if(customers.size()==1) {
			
			CreateTransactionOperation createTransactionOperation =
					new CreateTransactionOperation(
							customers.get(0).getId(), 
							createRequest.getAmount(),
							createRequest.getOperationType(),
							createRequest.getMoneyDirection());
			
			PendingOperation pendingOperation =
					pendingOperationService.create(
					user.getId(),
					createTransactionOperation,
					Status.PENDING);
			
			return new TransactionConfirmationResponse(
					"Confirm the Transaction", 
					pendingOperation.getOperationId(), 
					pendingOperation.getIntentKey(), 
					new TransactionPreview(
							PersonInfo.fromShopkeeper(user),
							PersonInfo.fromCustomer(customers.get(0)),
							createRequest.getAmount(),
							createRequest.getMoneyDirection()));
		}
		
		/*
		 * CASE 3 : When there are multiple customers with that name part
		 */
		else {
			CreateTransactionOperation createTransactionOperation = 
					new CreateTransactionOperation(
							null, 
							createRequest.getAmount(),
							createRequest.getOperationType(),
							createRequest.getMoneyDirection());
			
			
					PendingOperation pendingOperation =
							pendingOperationService.create(
					user.getId(),
					createTransactionOperation,
					Status.WAITING_FOR_CUSTOMER_SELECTION);
			
					
			return new CustomerSelectionResponse(pendingOperation.getOperationId(),
					"Select the Appropriate Customer",
					customers);
		}
	}


	@Override
	public String getIntentKey() {
		return CreateTransactionOperationRequest.intentKey;
	}

}
