package io.github.trip.shiv.vcledger.business.dtos.ledger.intent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCustomerBalanceQuery implements LedgerQuery{
	Long customerId;

}
