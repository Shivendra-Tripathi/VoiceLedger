package io.github.trip.shiv.vcledger.core.ledger.res;

import io.github.trip.shiv.vcledger.core.enums.ResponseType;

public interface LedgerResponse {
	ResponseType getResponseType();
	String getMessage();
}
