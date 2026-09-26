package io.github.trip.shiv.vcledger.core.ledger.requestexecutors;

import io.github.trip.shiv.vcledger.core.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.core.ledger.res.LedgerResponse;

public interface LedgerIntentRequestExecutor<T extends LedgerIntentRequest> {

	LedgerResponse execute(LedgerIntentRequest request);
	
	String getIntentKey();
}
