package io.github.trip.shiv.vcledger.core.ledger.processors.impls;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.github.trip.shiv.vcledger.core.ledger.operationexecutors.LedgerOperationExecutor;
import io.github.trip.shiv.vcledger.core.ledger.operations.LedgerOperation;
import io.github.trip.shiv.vcledger.core.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.core.exceptions.custom.internal.MissingLedgerOperationExecutorException;
import io.github.trip.shiv.vcledger.core.ledger.processors.interfaces.LedgerOperationExecutorDelegator;

@Component
public class LedgerOperationExecutorDelegatorImpl implements LedgerOperationExecutorDelegator{

	private final Map<String,LedgerOperationExecutor<?>> executors;
	
	
	//Automatically injects the List<LedgerOperationExecutor<?>>
	public LedgerOperationExecutorDelegatorImpl(
			List<LedgerOperationExecutor<?>> executors) {
	
		this.executors = 
				executors
				.stream()
				.collect(
						Collectors.toMap(
								executor -> executor.getIntentKey(),
								executor -> executor
								)
						);
	}
	
	@Override
	public LedgerResponse delegate(LedgerOperation ledgerOperation) {
	
		//Load the proper Executor
		LedgerOperationExecutor<?> executor = executors.get(ledgerOperation.getIntentKey());
		
		//When no proper executor found
		if(executor==null) {
			throw new MissingLedgerOperationExecutorException(
					"No LedgerOperationExecutor found for the request with intent : "+ledgerOperation.getIntentKey());
		}
		
		//Pass to proper executor
		return executor.execute(ledgerOperation);
	}
}
