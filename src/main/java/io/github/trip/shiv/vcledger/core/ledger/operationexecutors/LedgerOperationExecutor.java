package io.github.trip.shiv.vcledger.core.ledger.operationexecutors;

import io.github.trip.shiv.vcledger.core.ledger.operations.LedgerOperation;
import io.github.trip.shiv.vcledger.core.ledger.res.LedgerResponse;

public interface LedgerOperationExecutor<T extends LedgerOperation> {
	LedgerResponse execute(LedgerOperation ledgerOperation);
	
	String getIntentKey();
}
