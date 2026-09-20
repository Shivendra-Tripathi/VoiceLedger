package io.github.trip.shiv.vcledger.business.dtos.ledger.res;

public interface ConfirmationResponse extends LedgerResponse {

    String getOperationId();

    String getOperationType();
}
