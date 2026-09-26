package io.github.trip.shiv.vcledger.core.ledger.res;

import java.util.List;

import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.CustomerBalanceData;
import io.github.trip.shiv.vcledger.core.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomersBalanceInfoResponse implements InfoResponse {

  
    private String message;

    private List<CustomerBalanceData> customers;
    

	@Override
	public ResponseType getResponseType() {
		// TODO Auto-generated method stub
		return ResponseType.CUSTOMER_BALANCE_INFORMATION;
	}
}