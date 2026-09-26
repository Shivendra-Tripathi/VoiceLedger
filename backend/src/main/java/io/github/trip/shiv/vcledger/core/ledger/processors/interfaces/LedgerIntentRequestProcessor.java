package io.github.trip.shiv.vcledger.core.ledger.processors.interfaces;

import io.github.trip.shiv.vcledger.core.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.core.ledger.res.LedgerResponse;

public interface LedgerIntentRequestProcessor {
	
	LedgerResponse process(LedgerIntentRequest request);
}
