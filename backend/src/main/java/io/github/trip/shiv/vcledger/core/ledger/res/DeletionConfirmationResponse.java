package io.github.trip.shiv.vcledger.core.ledger.res;


import java.time.Instant;

import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.OperationPreview;
import io.github.trip.shiv.vcledger.core.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeletionConfirmationResponse implements ConfirmationResponse {

    private ResponseType responseType;
    private String message;
    private String operationId;
    private Instant expiresAt;
    private String operationType;

    private OperationPreview operation;

    public DeletionConfirmationResponse(
            String message,
            String operationId,
            Instant expiresAt,
            String operationType,
            OperationPreview operation
    ) {
        this.responseType = ResponseType.CONFIRMATION;
        this.message = message;
        this.operationId = operationId;
        this.expiresAt = expiresAt;
        this.operationType = operationType;
        this.operation = operation;
    }
}