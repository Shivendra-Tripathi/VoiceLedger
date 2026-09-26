package io.github.trip.shiv.vcledger.core.ledger.requestparsers;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.core.ledger.req.LedgerIntentRequest;

public interface LedgerIntentRequestParser<T extends LedgerIntentRequest> {
	
	public T parse(JsonNode json);
	public String getIntentKey();
}
