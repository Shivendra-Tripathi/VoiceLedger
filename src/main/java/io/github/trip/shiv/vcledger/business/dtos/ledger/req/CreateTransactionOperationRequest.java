package io.github.trip.shiv.vcledger.business.dtos.ledger.req;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.trip.shiv.vcledger.business.enums.MoneyDirection;
import io.github.trip.shiv.vcledger.business.enums.OperationType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class CreateTransactionOperationRequest implements LedgerOperationRequest{
	
	
	private  final String customerName;
	
	
	private  final BigDecimal amount;
	
	
	private  final MoneyDirection moneyDirection;
	
	@JsonIgnore
	public static final String intentKey = "CREATE_TRANSACTION";

	
	@Override
	public OperationType getOperationType() {
		// TODO Auto-generated method stub
		return OperationType.CREATE_TRANSACTION;
	}


	@Override
	public String getIntentKey() {
		return intentKey;
	}




	
	

	
}
