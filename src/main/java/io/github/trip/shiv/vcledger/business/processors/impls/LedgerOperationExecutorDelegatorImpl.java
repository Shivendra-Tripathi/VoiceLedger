package io.github.trip.shiv.vcledger.business.processors.impls;

import org.springframework.stereotype.Service;

import io.github.trip.shiv.vcledger.business.dtos.ledger.intent.CreateTransactionOperation;
import io.github.trip.shiv.vcledger.business.dtos.ledger.intent.LedgerOperation;
import io.github.trip.shiv.vcledger.business.dtos.ledger.operationexecutors.CreateTransactionOperationExecutor;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.business.processors.interfaces.LedgerOperationExecutorDelegator;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LedgerOperationExecutorDelegatorImpl implements LedgerOperationExecutorDelegator{

	private final CreateTransactionOperationExecutor createTransactionOperationExecutor;
	
	@Override
	public LedgerResponse delegate(LedgerOperation ledgerOperation) {
		
		if(ledgerOperation instanceof CreateTransactionOperation createRequest) {
			return createTransactionOperationExecutor.execute(createRequest);
		}
		
		throw new IllegalArgumentException("The ledger Operation Could not be handled");
	}

}
