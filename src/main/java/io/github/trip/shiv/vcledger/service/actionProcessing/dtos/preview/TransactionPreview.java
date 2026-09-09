package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.preview;

import java.math.BigDecimal;

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
public class TransactionPreview {

    private PersonInfo from;

    private PersonInfo to;

    private BigDecimal amount;

    private TransactionType transactionType;

    private MoneyDirection moneyDirection;
}