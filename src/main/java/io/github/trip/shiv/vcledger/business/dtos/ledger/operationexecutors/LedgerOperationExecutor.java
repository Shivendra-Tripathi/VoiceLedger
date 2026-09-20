package io.github.trip.shiv.vcledger.business.dtos.ledger.operationexecutors;

import io.github.trip.shiv.vcledger.business.dtos.ledger.operations.LedgerOperation;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;

public interface LedgerOperationExecutor<T extends LedgerOperation> {
	LedgerResponse execute(LedgerOperation ledgerOperation);
	
	String getIntentKey();
}
