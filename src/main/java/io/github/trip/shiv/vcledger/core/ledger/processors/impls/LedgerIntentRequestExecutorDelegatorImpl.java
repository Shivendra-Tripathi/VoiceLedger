package io.github.trip.shiv.vcledger.core.ledger.processors.impls;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.github.trip.shiv.vcledger.core.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.core.ledger.requestexecutors.LedgerIntentRequestExecutor;
import io.github.trip.shiv.vcledger.core.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.core.exceptions.custom.internal.MissingLedgerIntentRequestExecutorException;
import io.github.trip.shiv.vcledger.core.ledger.processors.interfaces.LedgerIntentRequestExecutorDelegator;

@Component
public class LedgerIntentRequestExecutorDelegatorImpl implements LedgerIntentRequestExecutorDelegator{

	
	private final Map<String,LedgerIntentRequestExecutor<?>> executors;
	
	
	//Automatically injects the dependencies for the List<LedgerIntentRequestExecutor>
	public LedgerIntentRequestExecutorDelegatorImpl(
			List<LedgerIntentRequestExecutor<?>> executors) {
		
		this.executors = 
				executors
				.stream()
				.collect(
						Collectors.toMap(
							executor -> executor.getIntentKey(), 
							executor -> executor)
						);
	}
	
	
	
	@Override
	public LedgerResponse delegate(LedgerIntentRequest request) {
		
		//Load the proper executor
		LedgerIntentRequestExecutor<?> executor = executors.get(request.getIntentKey());
		
		//Throw if no Proper Delegator Exists
		if(executor==null) {
			throw new MissingLedgerIntentRequestExecutorException(
					"No LedgerIntentRequestExecutor found for the request with intent : "+request.getIntentKey());
		}
		
		return executor.execute(request);
	}

}
