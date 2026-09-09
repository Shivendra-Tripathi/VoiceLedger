package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.response;

import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.OperationType;
import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.ResponseType;
import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.preview.OperationResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeletionSuccessResponse implements SuccessResponse {

    private ResponseType responseType;
    private String message;

    private OperationType operationType;

    private OperationResult operation;

    public DeletionSuccessResponse(
            String message,
            OperationType operationType,
            OperationResult operation
    ) {
        this.responseType = ResponseType.SUCCESS;
        this.message = message;
        this.operationType = operationType;
        this.operation = operation;
    }
}