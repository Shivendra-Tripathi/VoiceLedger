package io.github.trip.shiv.vcledger.core.ledger.requestparsers;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.trip.shiv.vcledger.core.ledger.req.CreateTransactionOperationRequest;
import io.github.trip.shiv.vcledger.core.enums.MoneyDirection;
import io.github.trip.shiv.vcledger.core.enums.TransactionType;

@Component
public class CreateTransactionOperationRequestParser implements LedgerIntentRequestParser<CreateTransactionOperationRequest> {

	@Override
	public CreateTransactionOperationRequest parse(JsonNode json) {
		String customerName = json.path("customerName").asText();
		BigDecimal amount = json.path("amount").decimalValue();
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
