package io.github.trip.shiv.vcledger.business.dtos.ledger.req;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.business.enums.OperationType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteLastTransactionOperationRequest implements LedgerOperationRequest{

	@Override
	public OperationType getOperationType() {
		// TODO Auto-generated method stub
		return OperationType.DELETE_LAST_TRANSACTION;
	}
	
	
	public static DeleteLastTransactionOperationRequest parse(JsonNode json) {
		//TODO : to be implemented in future
		return null;
	}
}
