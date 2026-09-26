package io.github.trip.shiv.vcledger.core.ledger.requestparsers;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.core.ledger.req.CustomerBalanceQueryRequest;

@Component
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
