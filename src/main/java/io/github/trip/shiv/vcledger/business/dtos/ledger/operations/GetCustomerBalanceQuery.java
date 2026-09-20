package io.github.trip.shiv.vcledger.business.dtos.ledger.operations;

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
