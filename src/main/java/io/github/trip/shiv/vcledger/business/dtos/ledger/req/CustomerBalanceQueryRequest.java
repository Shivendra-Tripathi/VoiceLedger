package io.github.trip.shiv.vcledger.business.dtos.ledger.req;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomerBalanceQueryRequest implements LedgerQueryRequest{
	
	private final String customerName;
	
	
	/*
	 * For Parsing the Json
	 */
	public static CustomerBalanceQueryRequest parse(JsonNode json) {
		String customerName = json.path("customerName").asText();
		
		return new CustomerBalanceQueryRequest(customerName);
	}
}
