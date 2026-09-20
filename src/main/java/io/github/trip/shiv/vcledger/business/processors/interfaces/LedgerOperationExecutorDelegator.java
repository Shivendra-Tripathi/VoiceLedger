package io.github.trip.shiv.vcledger.business.processors.interfaces;

import io.github.trip.shiv.vcledger.business.dtos.ledger.operations.LedgerOperation;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;

public interface LedgerOperationExecutorDelegator {

	LedgerResponse delegate(LedgerOperation ledgerOperation);
}
