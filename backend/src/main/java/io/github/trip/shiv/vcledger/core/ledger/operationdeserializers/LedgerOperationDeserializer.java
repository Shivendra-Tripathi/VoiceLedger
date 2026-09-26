package io.github.trip.shiv.vcledger.core.ledger.operationdeserializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.core.ledger.operations.LedgerOperation;

public interface LedgerOperationDeserializer<T extends LedgerOperation> {
	
	String getIntentKey();
	T deserialize(JsonNode node) throws JsonProcessingException, IllegalArgumentException;
}
