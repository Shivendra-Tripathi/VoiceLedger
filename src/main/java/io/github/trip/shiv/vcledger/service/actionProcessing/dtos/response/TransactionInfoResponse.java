package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.response;

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
public class TransactionInfoResponse implements InfoResponse {

    private ResponseType responseType;
    private String message;

    private TransactionData transaction;

    public TransactionInfoResponse(
            String message,
            TransactionData transaction
    ) {
        this.responseType = ResponseType.INFORMATION;
        this.message = message;
        this.transaction = transaction;
    }
}