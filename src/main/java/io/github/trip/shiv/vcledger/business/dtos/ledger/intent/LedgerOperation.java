package io.github.trip.shiv.vcledger.business.dtos.ledger.intent;

import io.github.trip.shiv.vcledger.business.enums.OperationType;

public interface LedgerOperation extends LedgerIntent{

    OperationType getType();

}