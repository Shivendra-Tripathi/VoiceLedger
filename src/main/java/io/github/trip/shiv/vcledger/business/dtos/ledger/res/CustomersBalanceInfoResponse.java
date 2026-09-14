package io.github.trip.shiv.vcledger.business.dtos.ledger.res;

import java.util.List;

import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.CustomerBalanceData;
import io.github.trip.shiv.vcledger.business.enums.ResponseType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomersBalanceInfoResponse implements InfoResponse {

  
    private String message;

    private List<CustomerBalanceData> customers;

	@Override
	public ResponseType getResponseType() {
		// TODO Auto-generated method stub
		return ResponseType.INFORMATION;
	}
}