package io.github.trip.shiv.vcledger.business.dtos.ledger.executors;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.trip.shiv.vcledger.business.dtos.ledger.intent.CreateTransactionOperation;
import io.github.trip.shiv.vcledger.business.dtos.ledger.req.CreateTransactionOperationRequest;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.CustomerNotFoundFailureResponse;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.CustomerSelectionResponse;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.TransactionConfirmationResponse;
import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.PersonInfo;
import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.TransactionPreview;
import io.github.trip.shiv.vcledger.business.utilities.SecurityUtils;
import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.PendingOperation;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.service.CustomerService;
import io.github.trip.shiv.vcledger.service.PendingOperationService;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class CreateTransactionRequestExecutor implements LedgerIntentRequestExecutor<CreateTransactionOperationRequest> {

	
	private final SecurityUtils securityUtils;
	private final CustomerService customerService;
	private final PendingOperationService pendingOperationService;

	
	@Override
	public LedgerResponse execute(CreateTransactionOperationRequest createRequest) {
		//Load the authenticated User
		User user = securityUtils.getAuthenticatedUser();
		
		//Load all the Customers with the name
		List<Customer> customers = 
				customerService.searchCustomers(
						user.getEmail(),
						createRequest.getCustomerName());
		
		/*
		 * CASE 1 : When the Customer Name is not found in the database
		 */
		if(customers.isEmpty()) {
			return new CustomerNotFoundFailureResponse(createRequest.getCustomerName());
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
					createTransactionOperation);
			
			return new TransactionConfirmationResponse(
					"Confirm the Transaction", 
					pendingOperation.getOperationId(), 
					pendingOperation.getOperationType(), 
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
					createTransactionOperation);
					
			return new CustomerSelectionResponse("Select the Appropriate Customer", customers);
		}
	}

}
