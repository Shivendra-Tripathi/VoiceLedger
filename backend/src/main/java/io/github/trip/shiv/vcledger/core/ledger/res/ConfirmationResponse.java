package io.github.trip.shiv.vcledger.core.ledger.res;

public interface ConfirmationResponse extends LedgerResponse {

    String getOperationId();

    String getOperationType();
}
