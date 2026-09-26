package io.github.trip.shiv.vcledger.core.ledger.requestexecutors;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.github.trip.shiv.vcledger.core.ledger.req.CustomerBalanceQueryRequest;
import io.github.trip.shiv.vcledger.core.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.core.ledger.res.CustomerNotFoundFailureResponse;
import io.github.trip.shiv.vcledger.core.ledger.res.CustomersBalanceInfoResponse;
import io.github.trip.shiv.vcledger.core.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.CustomerBalanceData;
import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.PersonInfo;
import io.github.trip.shiv.vcledger.core.exceptions.custom.internal.LedgerIntentRequestExecutorMismatchException;
import io.github.trip.shiv.vcledger.core.utilities.SecurityUtils;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.service.CustomerNameMatcherService;
import io.github.trip.shiv.vcledger.service.CustomerService;
import io.github.trip.shiv.vcledger.service.TransactionService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerBalanceQueryRequestExecutor implements LedgerIntentRequestExecutor<CustomerBalanceQueryRequest>{

	private final SecurityUtils securityUtils;
	private final CustomerService customerService;
	private final TransactionService transactionService;
	private final CustomerNameMatcherService customerNameMatcherService;
	
	
	private final static Logger logger =
			LoggerFactory.getLogger(CustomerBalanceQueryRequestExecutor.class);
	@Override
	public LedgerResponse execute(LedgerIntentRequest request) {
		
		
		//Throw if Wrong Request is Passes to this Executor
		if( ! (request instanceof CustomerBalanceQueryRequest 
				&& request.getIntentKey().equals(CustomerBalanceQueryRequest.intentKey) )) {
			throw new LedgerIntentRequestExecutorMismatchException(
					"The Executor :"+getClass().getName()
			+" can't handle the request with intent :" + request.getIntentKey());
		}
				
		CustomerBalanceQueryRequest queryRequest = 
				(CustomerBalanceQueryRequest)request;

		
		//Load the authenticated User
		User user = securityUtils.getAuthenticatedUser();
		
		//Load all the Customers with the name
		List<CustomerBalanceData> customers = 
				customerNameMatcherService
				.getMatchingCustomers(user.getId(), queryRequest.getCustomerName())
				.stream()
				.map(
						customer -> new CustomerBalanceData(
								PersonInfo.fromCustomer(customer),
								transactionService.getCustomerBalance(user.getId(), customer.getId()))
						)
				.toList();
		
		/*
		 * CASE 1 : When the Customer Name is not found in the database
		 */
		
		logger.info("Returning the response to the Customer Balance Query ");
		if(customers.isEmpty()) {
			return new CustomerNotFoundFailureResponse(queryRequest.getCustomerName());
		}
		
		/*
		 * CASE 2 : When the Customer Name is found i.e more than or equal to 1 Customer
		 */
		return new CustomersBalanceInfoResponse("Balance", customers);
	}



	@Override
	public String getIntentKey() {
		return CustomerBalanceQueryRequest.intentKey;
	}

}
