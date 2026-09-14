package io.github.trip.shiv.vcledger.business.dtos.ledger.res;

import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.TransactionPreview;
import io.github.trip.shiv.vcledger.business.enums.OperationType;
import io.github.trip.shiv.vcledger.business.enums.ResponseType;
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

    private OperationType operationType;

    private TransactionPreview transaction;

  
	@Override
	public ResponseType getResponseType() {
		// TODO Auto-generated method stub
		return ResponseType.CONFIRMATION;
	}
}