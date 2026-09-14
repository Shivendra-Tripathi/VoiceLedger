package io.github.trip.shiv.vcledger.business.dtos.ledger.req;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.business.enums.MoneyDirection;
import io.github.trip.shiv.vcledger.business.enums.OperationType;
import io.github.trip.shiv.vcledger.business.enums.TransactionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class CreateTransactionOperationRequest implements LedgerOperationRequest{
	
	
	private  final String customerName;
	
	
	private  final BigDecimal amount;
	
	
	private  final MoneyDirection moneyDirection;

	
	@Override
	public OperationType getOperationType() {
		// TODO Auto-generated method stub
		return OperationType.CREATE_TRANSACTION;
	}


	
	/*
	 * Creates the CreateTransactionOperationRequest by parsing the Json
	 */
	public static CreateTransactionOperationRequest parse(JsonNode json) {
		String customerName = json.path("customerName").asText();
		BigDecimal amount = json.path("amount").decimalValue();
		TransactionType transactionType = TransactionType.valueOf(json.path("transactionType").asText());
		
		return new CreateTransactionOperationRequest(
				customerName,
				amount,
				MoneyDirection.from(transactionType));
	}

	
}
