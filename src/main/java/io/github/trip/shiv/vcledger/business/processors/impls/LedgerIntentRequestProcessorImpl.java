package io.github.trip.shiv.vcledger.business.processors.impls;

import org.springframework.stereotype.Service;

import io.github.trip.shiv.vcledger.business.dtos.ledger.executors.CreateTransactionRequestExecutor;
import io.github.trip.shiv.vcledger.business.dtos.ledger.executors.CustomerBalanceQueryRequestExecutor;
import io.github.trip.shiv.vcledger.business.dtos.ledger.req.CreateTransactionOperationRequest;
import io.github.trip.shiv.vcledger.business.dtos.ledger.req.CustomerBalanceQueryRequest;
import io.github.trip.shiv.vcledger.business.dtos.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.business.processors.interfaces.LedgerIntentRequestProcessor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LedgerIntentRequestProcessorImpl implements LedgerIntentRequestProcessor{

	
	private final CustomerBalanceQueryRequestExecutor customerBalanceQueryRequestExecutor;
	private final CreateTransactionRequestExecutor createTransactionRequestExecutor;
	
	@Override
	public LedgerResponse process(LedgerIntentRequest request) {
		
		//Creates a new Transaction and returns the appropriate response
		if(request instanceof CreateTransactionOperationRequest createRequest) {
			return createTransactionRequestExecutor.execute(createRequest);
		}
		
		if(request instanceof CustomerBalanceQueryRequest queryRequest) {
			return customerBalanceQueryRequestExecutor.execute(queryRequest);
		}
		
		throw new IllegalArgumentException("The LedgerIntentRequest can't be mapped ,"
		+" no mapping provided in LedgerIntentReuqestProcessorImpl");
	}

}
