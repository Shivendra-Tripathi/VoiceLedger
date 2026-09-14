package io.github.trip.shiv.vcledger.business.factories;


import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.trip.shiv.vcledger.business.dtos.ledger.intent.CreateTransactionOperation;
import io.github.trip.shiv.vcledger.business.dtos.ledger.intent.DeleteTransactionOperation;
import io.github.trip.shiv.vcledger.business.dtos.ledger.intent.LedgerOperation;
import io.github.trip.shiv.vcledger.entity.PendingOperation;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LedgerOperationFactory {

    private final ObjectMapper objectMapper;

    public  LedgerOperation fromPendingOperation(PendingOperation pendingOperation) {

        if (pendingOperation == null) {
            throw new IllegalArgumentException("Pending operation cannot be null");
        }

        if (pendingOperation.getOperationType() == null) {
            throw new IllegalArgumentException("Operation type cannot be null");
        }

        if (pendingOperation.getPayload() == null
                || pendingOperation.getPayload().isBlank()) {
            throw new IllegalArgumentException("Operation payload cannot be empty");
        }

        try {

            return switch (pendingOperation.getOperationType()) {

                case CREATE_TRANSACTION ->
                        objectMapper.readValue(
                                pendingOperation.getPayload(),
                                CreateTransactionOperation.class
                        );

                case DELETE_TRANSACTION ->
                        objectMapper.readValue(
                                pendingOperation.getPayload(),
                                DeleteTransactionOperation.class
                        );
                default -> 
                throw new IllegalArgumentException("Unexpected value: " + pendingOperation.getOperationType());
                        
                
            };

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Failed to deserialize pending operation payload",
                    e
            );
        }
    }
}