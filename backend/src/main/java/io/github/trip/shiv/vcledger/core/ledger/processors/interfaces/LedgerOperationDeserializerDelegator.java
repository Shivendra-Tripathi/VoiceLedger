package io.github.trip.shiv.vcledger.core.ledger.processors.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.core.ledger.operations.LedgerOperation;

public interface LedgerOperationDeserializerDelegator {
	LedgerOperation delegate(String intent,JsonNode json) throws JsonProcessingException, IllegalArgumentException;
}
