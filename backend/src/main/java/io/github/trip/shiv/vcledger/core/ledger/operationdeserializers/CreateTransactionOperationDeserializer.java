package io.github.trip.shiv.vcledger.core.ledger.operationdeserializers;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.trip.shiv.vcledger.core.ledger.operations.CreateTransactionOperation;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreateTransactionOperationDeserializer implements LedgerOperationDeserializer<CreateTransactionOperation>{

	private final ObjectMapper objectMapper;
	
	@Override
	public CreateTransactionOperation deserialize(JsonNode json) throws JsonProcessingException {
		return objectMapper.treeToValue(
				json,
				CreateTransactionOperation.class);
	}

	@Override
	public String getIntentKey() {
		return CreateTransactionOperation.intentKey;
	}
}
