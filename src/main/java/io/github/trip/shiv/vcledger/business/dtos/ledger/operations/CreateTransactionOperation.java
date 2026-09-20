package io.github.trip.shiv.vcledger.business.dtos.ledger.operations;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.trip.shiv.vcledger.business.enums.MoneyDirection;
import io.github.trip.shiv.vcledger.business.enums.OperationType;
import lombok.Getter;

@Getter
public class CreateTransactionOperation implements LedgerOperation {

    private final Long customerId;
    private final BigDecimal amount;
    private final OperationType operationType;
    private final MoneyDirection moneyDirection;
    
    @JsonIgnore
    public static final String intentKey = "CREATE_TRANSACTION";

    @JsonCreator
    public CreateTransactionOperation(
            @JsonProperty("customerId") Long customerId,
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("operationType") OperationType operationType,
            @JsonProperty("moneyDirection") MoneyDirection moneyDirection) {

        this.customerId = customerId;
        this.amount = amount;
        this.operationType = operationType;
        this.moneyDirection = moneyDirection;
    }
    
    @JsonIgnore
    @Override
    public String getIntentKey() {
        return intentKey;
    }
}