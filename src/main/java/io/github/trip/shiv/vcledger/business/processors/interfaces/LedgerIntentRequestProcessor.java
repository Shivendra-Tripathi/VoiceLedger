package io.github.trip.shiv.vcledger.business.processors.interfaces;

import io.github.trip.shiv.vcledger.business.dtos.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;

public interface LedgerIntentRequestProcessor {
	
	LedgerResponse process(LedgerIntentRequest request);
}
