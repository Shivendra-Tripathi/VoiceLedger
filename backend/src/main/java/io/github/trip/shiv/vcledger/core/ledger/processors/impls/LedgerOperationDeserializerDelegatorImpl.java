package io.github.trip.shiv.vcledger.core.ledger.processors.impls;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.core.ledger.operationdeserializers.LedgerOperationDeserializer;
import io.github.trip.shiv.vcledger.core.ledger.operations.LedgerOperation;
import io.github.trip.shiv.vcledger.core.exceptions.custom.internal.MissingLedgerOperationDeserializerException;
import io.github.trip.shiv.vcledger.core.ledger.processors.interfaces.LedgerOperationDeserializerDelegator;


@Component
public class LedgerOperationDeserializerDelegatorImpl implements LedgerOperationDeserializerDelegator{
	
	private final Map<String, LedgerOperationDeserializer<?>> deserializers;
	
	public LedgerOperationDeserializerDelegatorImpl(
			List<LedgerOperationDeserializer<?>> deserializers) {
		this.deserializers = 
				deserializers
				.stream()
				.collect(
						Collectors.toMap(
								deserializer -> deserializer.getIntentKey(),
								deserializer -> deserializer)
						);
	}

	@Override
	public LedgerOperation delegate(String intent , JsonNode json ) throws JsonProcessingException, IllegalArgumentException {
		
		//Get the proper deserializer
		LedgerOperationDeserializer<?> deserializer = deserializers.get(intent);
		
		if(deserializer==null) {
			throw new MissingLedgerOperationDeserializerException("The Deserializer(pendingOperation.json intent ---> LedgerOperation) is missing for intent : "
					+intent);
		}
		
		return deserializer.deserialize(json);
	}
	
	
	
}
