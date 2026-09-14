package io.github.trip.shiv.vcledger.business.dtos.ledger.res;

import io.github.trip.shiv.vcledger.business.enums.ResponseType;

public interface LedgerResponse {
	ResponseType getResponseType();
	String getMessage();
}
