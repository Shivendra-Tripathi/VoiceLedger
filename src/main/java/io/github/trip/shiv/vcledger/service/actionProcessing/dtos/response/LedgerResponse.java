package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.response;

import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.ResponseType;

public interface LedgerResponse {
	ResponseType getResponseType();
	String getMessage();
}
