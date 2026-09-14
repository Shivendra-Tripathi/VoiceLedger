package io.github.trip.shiv.vcledger.business.dtos.ledger.res;

import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.OperationResult;
import io.github.trip.shiv.vcledger.business.enums.OperationType;
import io.github.trip.shiv.vcledger.business.enums.ResponseType;
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