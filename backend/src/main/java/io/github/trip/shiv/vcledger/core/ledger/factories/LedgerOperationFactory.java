package io.github.trip.shiv.vcledger.core.ledger.factories;


import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.trip.shiv.vcledger.core.ledger.operations.LedgerOperation;
import io.github.trip.shiv.vcledger.core.ledger.processors.interfaces.LedgerOperationDeserializerDelegator;
import io.github.trip.shiv.vcledger.entity.PendingOperation;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LedgerOperationFactory {

	private final ObjectMapper objectMapper;
    private final LedgerOperationDeserializerDelegator ledgerOperationDeserializerDelegator; 
   
    
    

    public  LedgerOperation fromPendingOperation(PendingOperation pendingOperation) throws  JsonProcessingException {
    	
    	JsonNode json = objectMapper.readTree(pendingOperation.getPayload());
    	return ledgerOperationDeserializerDelegator.delegate(pendingOperation.getIntentKey(), json);
    }
    
    
    public String serialize(LedgerOperation operation) {

        try {
            return objectMapper.writeValueAsString(operation);

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Failed to serialize ledger operation",
                    e
            );
        }
    }
}