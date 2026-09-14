package io.github.trip.shiv.vcledger.business.dtos.ledger.res;

import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.TransactionData;
import io.github.trip.shiv.vcledger.business.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSuccessResponse implements SuccessResponse {

 
    private String message;

    private TransactionData transaction;

	@Override
	public ResponseType getResponseType() {
		return ResponseType.SUCCESS;
	}

    
}