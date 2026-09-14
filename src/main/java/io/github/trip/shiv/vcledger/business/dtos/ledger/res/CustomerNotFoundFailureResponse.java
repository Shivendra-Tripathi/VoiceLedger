package io.github.trip.shiv.vcledger.business.dtos.ledger.res;

import io.github.trip.shiv.vcledger.business.enums.ResponseType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CustomerNotFoundFailureResponse implements FailureResponse{

	
	private final String customerName;
	
	
	
	@Override
	public ResponseType getResponseType() {
		return ResponseType.FAILURE;
	}

	@Override
	public String getMessage() {
		return "Customer Not Found";
	}

}
