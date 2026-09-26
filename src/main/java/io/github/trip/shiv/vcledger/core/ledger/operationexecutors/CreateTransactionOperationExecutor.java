package io.github.trip.shiv.vcledger.core.ledger.operationexecutors;

import org.springframework.stereotype.Service;

import io.github.trip.shiv.vcledger.core.ledger.operations.CreateTransactionOperation;
import io.github.trip.shiv.vcledger.core.ledger.operations.LedgerOperation;
import io.github.trip.shiv.vcledger.core.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.core.ledger.res.TransactionSuccessResponse;
import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.PersonInfo;
import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.TransactionData;
import io.github.trip.shiv.vcledger.core.enums.MoneyDirection;
import io.github.trip.shiv.vcledger.core.enums.TransactionType;
import io.github.trip.shiv.vcledger.core.exceptions.custom.internal.LedgerIntentOperationExecutorMismatchException;
import io.github.trip.shiv.vcledger.core.utilities.SecurityUtils;
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
	public LedgerResponse execute(LedgerOperation operation) {
		
		
		//If wrong operation passed to this executor
		if(! (operation instanceof CreateTransactionOperation 
				&& operation.getIntentKey().equals(CreateTransactionOperation.intentKey))) {
			throw new LedgerIntentOperationExecutorMismatchException(
					"The Executor :"+getClass().getName()
					+" can't handle the LedgerOperation request of intent type :"+operation.getIntentKey());
		}
			
		CreateTransactionOperation createOperation =
				(CreateTransactionOperation)operation;
		
		
		User user = securityUtils.getAuthenticatedUser();
		Customer customer = customerService.getCustomerById(
				user.getId(), 
				createOperation.getCustomerId());
		
		Transaction transaction = transactionService.createTransaction(
				user.getId(),
				customer.getId(),
				createOperation.getAmount(),
				TransactionType.from(createOperation.getMoneyDirection()),
				"");
		
		
		
		System.out.println("Inside the CreateTransactionExecutor's execute method");
		return new TransactionSuccessResponse(
				"Transaction Done",
				new TransactionData(
						PersonInfo.fromShopkeeper(user),
						PersonInfo.fromCustomer(customer),
						transaction.getAmount(),
						MoneyDirection.from(transaction.getType()),
						transaction.getCreatedAt()));
	}

	@Override
	public String getIntentKey() {
		return CreateTransactionOperation.intentKey;
	}
	

}
