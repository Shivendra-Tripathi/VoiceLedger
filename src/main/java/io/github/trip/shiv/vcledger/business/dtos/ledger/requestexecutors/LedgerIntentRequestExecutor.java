package io.github.trip.shiv.vcledger.business.dtos.ledger.requestexecutors;

import io.github.trip.shiv.vcledger.business.dtos.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;

public interface LedgerIntentRequestExecutor<T extends LedgerIntentRequest> {

	LedgerResponse execute(LedgerIntentRequest request);
	
	String getIntentKey();
}
