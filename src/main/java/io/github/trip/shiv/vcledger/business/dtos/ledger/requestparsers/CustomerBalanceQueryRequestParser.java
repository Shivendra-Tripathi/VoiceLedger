package io.github.trip.shiv.vcledger.business.dtos.ledger.requestparsers;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.business.dtos.ledger.req.CustomerBalanceQueryRequest;

public class CustomerBalanceQueryRequestParser implements LedgerIntentRequestParser<CustomerBalanceQueryRequest> {

	@Override
	public CustomerBalanceQueryRequest parse(JsonNode json) {
		
		String customerName = json.path("customerName").asText();
		
		return new CustomerBalanceQueryRequest(customerName);
	}

	@Override
	public String getIntentKey() {
		return CustomerBalanceQueryRequest.intentKey;
	}

}
