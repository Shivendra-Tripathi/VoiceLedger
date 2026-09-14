package io.github.trip.shiv.vcledger.business.dtos.ledger.operationexecutors;

import org.springframework.stereotype.Service;

import io.github.trip.shiv.vcledger.business.dtos.ledger.intent.CreateTransactionOperation;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.TransactionSuccessResponse;
import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.PersonInfo;
import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.TransactionData;
import io.github.trip.shiv.vcledger.business.enums.MoneyDirection;
import io.github.trip.shiv.vcledger.business.enums.TransactionType;
import io.github.trip.shiv.vcledger.business.utilities.SecurityUtils;
import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.Transaction;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.service.CustomerService;
import io.github.trip.shiv.vcledger.service.TransactionService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CreateTransactionOperationExecutor implements LedgerOperationExecutor<CreateTransactionOperation>{

	private final SecurityUtils securityUtils;
	private final CustomerService customerService;
	private final TransactionService transactionService;
	
	@Override
	public LedgerResponse execute(CreateTransactionOperation createOperation) {
		
		
		User user = securityUtils.getAuthenticatedUser();
		Customer customer = customerService.getCustomerById(
				user.getEmail(), 
				createOperation.getCustomerId());
		
		Transaction transaction = transactionService.createTransaction(
				user.getId(),
				customer.getId(),
				createOperation.getAmount(),
				TransactionType.from(createOperation.getMoneyDirection()),
				"");
		
		
		return new TransactionSuccessResponse(
				"Transaction Done",
				new TransactionData(
						PersonInfo.fromShopkeeper(user),
						PersonInfo.fromCustomer(customer),
						transaction.getAmount(),
						MoneyDirection.from(transaction.getType()),
						transaction.getCreatedAt()));
	}
	

}
