package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.response;

import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.FailureReason;
import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.ResponseType;
import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.preview.TransactionData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFailureResponse implements FailureResponse {

    private ResponseType responseType;
    private String message;

    private FailureReason failureReason;

    private TransactionData transaction;

    public TransactionFailureResponse(
            String message,
            FailureReason failureReason,
            TransactionData transaction
    ) {
        this.responseType = ResponseType.FAILURE;
        this.message = message;
        this.failureReason = failureReason;
        this.transaction = transaction;
    }
}