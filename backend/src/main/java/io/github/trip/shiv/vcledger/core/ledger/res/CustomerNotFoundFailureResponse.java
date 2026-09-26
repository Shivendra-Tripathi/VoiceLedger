package io.github.trip.shiv.vcledger.core.ledger.res;

import io.github.trip.shiv.vcledger.core.enums.ResponseType;
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
