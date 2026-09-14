package io.github.trip.shiv.vcledger.business.dtos.ledger.executors;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.trip.shiv.vcledger.business.dtos.ledger.req.CustomerBalanceQueryRequest;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.CustomerNotFoundFailureResponse;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.CustomersBalanceInfoResponse;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.CustomerBalanceData;
import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.PersonInfo;
import io.github.trip.shiv.vcledger.business.utilities.SecurityUtils;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.service.CustomerService;
import io.github.trip.shiv.vcledger.service.TransactionService;
import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class CustomerBalanceQueryRequestExecutor implements LedgerIntentRequestExecutor<CustomerBalanceQueryRequest>{

	private final SecurityUtils securityUtils;
	private final CustomerService customerService;
	private final TransactionService transactionService;
	
	
	@Override
	public LedgerResponse execute(CustomerBalanceQueryRequest queryRequest) {
		
		//Load the authenticated User
		User user = securityUtils.getAuthenticatedUser();
		
		//Load all the Customers with the name
		List<CustomerBalanceData> customers = 
				customerService.searchCustomers(
						user.getEmail(),
						queryRequest.getCustomerName())
				.stream()
				.map(
						customer -> new CustomerBalanceData(
								PersonInfo.fromCustomer(customer),
								transactionService.getCustomerBalance(user.getEmail(), customer.getId()))
						)
				.toList();
		
		/*
		 * CASE 1 : When the Customer Name is not found in the database
		 */
		if(customers.isEmpty()) {
			return new CustomerNotFoundFailureResponse(queryRequest.getCustomerName());
		}
		
		/*
		 * CASE 2 : When the Customer Name is found i.e more than or equal to 1 Customer
		 */
		return new CustomersBalanceInfoResponse("Balance", customers);
	}

}
