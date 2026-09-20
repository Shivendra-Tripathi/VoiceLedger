package io.github.trip.shiv.vcledger.business.dtos.ledger.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.business.enums.OperationType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteLastTransactionOperationRequest implements LedgerOperationRequest{
	
	@JsonIgnore
	public static final String intentKey = "DELETE_TRANSACTION";
	
	
	@Override
	public OperationType getOperationType() {
		// TODO Auto-generated method stub
		return OperationType.DELETE_LAST_TRANSACTION;
	}
	
	
	public static DeleteLastTransactionOperationRequest parse(JsonNode json) {
		//TODO : to be implemented in future
		return null;
	}
	
	@Override
	public String getIntentKey() {
		return intentKey;
	}



}
