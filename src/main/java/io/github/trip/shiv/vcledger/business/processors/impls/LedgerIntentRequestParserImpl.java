package io.github.trip.shiv.vcledger.business.processors.impls;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.business.dtos.ledger.req.CreateTransactionOperationRequest;
import io.github.trip.shiv.vcledger.business.dtos.ledger.req.CustomerBalanceQueryRequest;
import io.github.trip.shiv.vcledger.business.dtos.ledger.req.LedgerIntentRequest;
import io.github.trip.shiv.vcledger.business.processors.interfaces.LedgerIntentRequestParser;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LedgerIntentRequestParserImpl implements LedgerIntentRequestParser{
	

	
	@Override
	public LedgerIntentRequest parse(JsonNode json) {
		
		return switch(json.path("intent").asText()) {
			case "CREATE_TRANSACTION" ->  CreateTransactionOperationRequest.parse(json);
			case "CUSTOMER_BALANCE" -> CustomerBalanceQueryRequest.parse(json);
			default -> throw new IllegalArgumentException("Unexpected value: " + json.path("intent").asText());
		};
	}
	
	


}
