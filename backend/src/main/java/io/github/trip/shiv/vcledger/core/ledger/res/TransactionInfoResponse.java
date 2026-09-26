package io.github.trip.shiv.vcledger.core.ledger.res;

import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.TransactionData;
import io.github.trip.shiv.vcledger.core.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionInfoResponse implements InfoResponse {

	private String message;

    private TransactionData transaction;

	@Override
	public ResponseType getResponseType() {
		return ResponseType.INFORMATION;
	}

    
}