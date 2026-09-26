package io.github.trip.shiv.vcledger.core.ledger.req;

import io.github.trip.shiv.vcledger.core.enums.OperationType;

public interface LedgerOperationRequest extends LedgerIntentRequest{
	OperationType getOperationType();
}
