package io.github.trip.shiv.vcledger.business.dtos.ledger.requestparsers;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.business.dtos.ledger.req.LedgerIntentRequest;

public interface LedgerIntentRequestParser<T extends LedgerIntentRequest> {
	
	public T parse(JsonNode json);
	public String getIntentKey();
}
