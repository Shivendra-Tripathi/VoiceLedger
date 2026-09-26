package io.github.trip.shiv.vcledger.core.ledger.res;

import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.TransactionPreview;
import io.github.trip.shiv.vcledger.core.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionConfirmationResponse implements ConfirmationResponse {


    private String message;

    private String operationId;

    private String operationType;

    private TransactionPreview transaction;

  
	@Override
	public ResponseType getResponseType() {
		// TODO Auto-generated method stub
		return ResponseType.CONFIRMATION;
	}
}