package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.response;

import java.time.Instant;

import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.OperationType;

public interface ConfirmationResponse extends LedgerResponse {

    String getOperationId();

    String getConfirmationToken();

    Instant getExpiresAt();

    OperationType getOperationType();
}
