package io.github.trip.shiv.vcledger.business.factories;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.trip.shiv.vcledger.business.dtos.ledger.intent.LedgerOperation;
import io.github.trip.shiv.vcledger.entity.PendingOperation;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PendingOperationFactory {

    private final ObjectMapper objectMapper;

    /**
     * Converts a LedgerOperation into a PendingOperation
     * that can be persisted in the database.
     */
    public PendingOperation fromLedgerOperation(LedgerOperation operation , Instant createdAt , Instant expiresAt) {

        if (operation == null) {
            throw new IllegalArgumentException(
                    "Ledger operation cannot be null"
            );
        }

        String payload = serialize(operation);

        PendingOperation pendingOperation = new PendingOperation();

        pendingOperation.setOperationType(operation.getType());
        pendingOperation.setPayload(payload);
        pendingOperation.setStatus(PendingOperation.Status.PENDING);

       
        pendingOperation.setCreatedAt(createdAt);
        pendingOperation.setExpiresAt(expiresAt);

        return pendingOperation;
    }

    /**
     * Converts the operation into JSON that can be stored
     * in PendingOperation.payload.
     */
    private String serialize(LedgerOperation operation) {

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

