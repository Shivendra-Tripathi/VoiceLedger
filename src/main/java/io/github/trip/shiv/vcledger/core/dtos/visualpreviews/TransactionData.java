package io.github.trip.shiv.vcledger.core.dtos.visualpreviews;

import java.math.BigDecimal;
import java.time.Instant;

import io.github.trip.shiv.vcledger.core.enums.MoneyDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionData {


    private PersonInfo shopkeeper;
    private PersonInfo customer;

    private BigDecimal amount;

    private MoneyDirection moneyDirection;

    private Instant createdAt;
    
    
}