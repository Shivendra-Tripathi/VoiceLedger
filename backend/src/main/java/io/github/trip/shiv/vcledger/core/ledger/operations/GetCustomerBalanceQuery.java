package io.github.trip.shiv.vcledger.core.ledger.operations;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCustomerBalanceQuery implements LedgerQuery{
	Long customerId;

	@JsonIgnore
	private final String intentKey = "CUSTOMER_BALANCE";
	
	@Override
	public String getIntentKey() {
		return intentKey;
	}

}
