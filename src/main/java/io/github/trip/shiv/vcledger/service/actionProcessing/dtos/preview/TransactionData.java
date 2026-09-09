package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.preview;

import java.math.BigDecimal;
import java.time.Instant;

import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.MoneyDirection;
import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionData {

    private Long transactionId;

    private PersonInfo from;
    private PersonInfo to;

    private BigDecimal amount;

    private TransactionType transactionType;

    private MoneyDirection moneyDirection;

    private Instant createdAt;
}