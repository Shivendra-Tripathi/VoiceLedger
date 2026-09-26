package io.github.trip.shiv.vcledger.core.dtos.visualpreviews;

import java.math.BigDecimal;

import io.github.trip.shiv.vcledger.core.enums.MoneyDirection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPreview {

    private PersonInfo shopkeeper;

    private PersonInfo customer	;

    private BigDecimal amount;

    private MoneyDirection moneyDirection;
}