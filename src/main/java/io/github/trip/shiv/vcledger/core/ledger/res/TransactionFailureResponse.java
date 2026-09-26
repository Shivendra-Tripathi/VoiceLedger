package io.github.trip.shiv.vcledger.core.ledger.res;

import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.TransactionData;
import io.github.trip.shiv.vcledger.core.enums.FailureReason;
import io.github.trip.shiv.vcledger.core.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFailureResponse implements FailureResponse {

	private String message;

    private FailureReason failureReason;

    private TransactionData transaction;


	@Override
	public ResponseType getResponseType() {
		// TODO Auto-generated method stub
		return ResponseType.FAILURE;
	}
}