package io.github.trip.shiv.vcledger.core.ledger.req;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomerBalanceQueryRequest implements LedgerQueryRequest{
	
	private final String customerName;
	
	@JsonIgnore
	public static final String intentKey = "CUSTOMER_BALANCE";
	
	
	@Override
	public String getIntentKey() {
		return intentKey;
	}

}
