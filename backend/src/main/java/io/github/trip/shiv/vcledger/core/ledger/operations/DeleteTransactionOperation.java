package io.github.trip.shiv.vcledger.core.ledger.operations;


import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteTransactionOperation implements LedgerOperation {
	
	
	@JsonIgnore
	private final String intentKey = "DELETE_TRANSACTION";
	
    private final Long transactionId;

	@Override
	public String getIntentKey() {
		return intentKey;
	}
}