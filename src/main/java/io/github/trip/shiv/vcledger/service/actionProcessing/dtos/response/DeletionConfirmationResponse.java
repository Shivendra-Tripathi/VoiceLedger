package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.response;


import java.time.Instant;

import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.OperationType;
import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.ResponseType;
import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.preview.OperationPreview;
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
    private String confirmationToken;
    private Instant expiresAt;
    private OperationType operationType;

    private OperationPreview operation;

    public DeletionConfirmationResponse(
            String message,
            String operationId,
            String confirmationToken,
            Instant expiresAt,
            OperationType operationType,
            OperationPreview operation
    ) {
        this.responseType = ResponseType.CONFIRMATION;
        this.message = message;
        this.operationId = operationId;
        this.confirmationToken = confirmationToken;
        this.expiresAt = expiresAt;
        this.operationType = operationType;
        this.operation = operation;
    }
}