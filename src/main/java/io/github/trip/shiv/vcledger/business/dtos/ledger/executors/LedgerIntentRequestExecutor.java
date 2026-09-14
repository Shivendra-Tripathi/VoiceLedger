package io.github.trip.shiv.vcledger.business.dtos.ledger.executors;

import io.github.trip.shiv.vcledger.business.dtos.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;

public interface LedgerIntentRequestExecutor<T extends LedgerIntentRequest> {
	
	LedgerResponse execute(T request);
}
