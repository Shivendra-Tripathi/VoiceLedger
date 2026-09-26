package io.github.trip.shiv.vcledger.core.ledger.processors.interfaces;

import io.github.trip.shiv.vcledger.core.ledger.operations.LedgerOperation;
import io.github.trip.shiv.vcledger.core.ledger.res.LedgerResponse;

public interface LedgerOperationExecutorDelegator {

	LedgerResponse delegate(LedgerOperation ledgerOperation);
}
