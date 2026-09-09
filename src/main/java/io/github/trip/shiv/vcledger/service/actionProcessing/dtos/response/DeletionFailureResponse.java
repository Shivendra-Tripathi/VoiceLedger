package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.response;

import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.FailureReason;
import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.OperationType;
import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeletionFailureResponse implements FailureResponse {

    private ResponseType responseType;
    private String message;

    private FailureReason failureReason;

    private OperationType operationType;

    public DeletionFailureResponse(
            String message,
            FailureReason failureReason,
            OperationType operationType
    ) {
        this.responseType = ResponseType.FAILURE;
        this.message = message;
        this.failureReason = failureReason;
        this.operationType = operationType;
    }
}