package io.github.trip.shiv.vcledger.business.dtos.ledger.req;

import io.github.trip.shiv.vcledger.business.enums.OperationType;

public interface LedgerOperationRequest extends LedgerIntentRequest{
	OperationType getOperationType();
}
