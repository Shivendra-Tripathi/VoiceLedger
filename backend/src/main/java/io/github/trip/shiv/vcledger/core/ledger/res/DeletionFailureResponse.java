package io.github.trip.shiv.vcledger.core.ledger.res;

import io.github.trip.shiv.vcledger.core.enums.FailureReason;
import io.github.trip.shiv.vcledger.core.enums.OperationType;
import io.github.trip.shiv.vcledger.core.enums.ResponseType;
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