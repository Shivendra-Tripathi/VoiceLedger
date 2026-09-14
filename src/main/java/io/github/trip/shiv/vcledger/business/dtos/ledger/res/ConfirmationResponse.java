package io.github.trip.shiv.vcledger.business.dtos.ledger.res;

import io.github.trip.shiv.vcledger.business.enums.OperationType;

public interface ConfirmationResponse extends LedgerResponse {

    String getOperationId();

    OperationType getOperationType();
}
