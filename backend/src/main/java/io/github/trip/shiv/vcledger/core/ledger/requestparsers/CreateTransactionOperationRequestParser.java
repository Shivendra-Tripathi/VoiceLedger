package io.github.trip.shiv.vcledger.core.ledger.requestparsers;

import java.math.BigDecimal;

import io.github.trip.shiv.vcledger.core.exceptions.custom.business.MissingFieldInIntentException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.core.ledger.req.CreateTransactionOperationRequest;
import io.github.trip.shiv.vcledger.core.enums.MoneyDirection;
import io.github.trip.shiv.vcledger.core.enums.TransactionType;

@Component
public class CreateTransactionOperationRequestParser implements LedgerIntentRequestParser<CreateTransactionOperationRequest> {

	@Override
	public CreateTransactionOperationRequest parse(JsonNode json) {
		String customerName = json.get("customerName").asText();
		BigDecimal amount = json.get("amount").decimalValue();

		//Throw for Missing CustomerName
		if(customerName == null || customerName.isEmpty()){
			throw new MissingFieldInIntentException("customerName not found in the intent request");
		}

		//Throw for Missing Balance or ZERO Balance
		if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
			throw new MissingFieldInIntentException("amount not found in the intent request");
		}

		TransactionType transactionType = TransactionType.valueOf(json.path("transactionType").asText());
		
		return new CreateTransactionOperationRequest(
				customerName,
				amount,
				MoneyDirection.from(transactionType));
	}

	@Override
	public String getIntentKey() {
		// TODO Auto-generated method stub
		return CreateTransactionOperationRequest.intentKey;
	}

}
