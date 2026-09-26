package io.github.trip.shiv.vcledger.core.ledger.requestparsers;

import io.github.trip.shiv.vcledger.core.exceptions.custom.business.MissingFieldInIntentException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.core.ledger.req.CustomerBalanceQueryRequest;

@Component
public class CustomerBalanceQueryRequestParser implements LedgerIntentRequestParser<CustomerBalanceQueryRequest> {

	@Override
	public CustomerBalanceQueryRequest parse(JsonNode json) {
		
		String customerName = json.get("customerName").asText();

		//Throw if the CustomerName is Missing from the JSON Intent Request
		if(customerName == null || customerName.isEmpty()){
			throw new MissingFieldInIntentException("CustomerName is not found in the intent.");
		}

		return new CustomerBalanceQueryRequest(customerName);
	}

	@Override
	public String getIntentKey() {
		return CustomerBalanceQueryRequest.intentKey;
	}

}
