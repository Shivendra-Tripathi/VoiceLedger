package io.github.trip.shiv.vcledger.business.dtos.visualpreviews;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBalanceData {

    private PersonInfo customer;
    private BigDecimal balance;
}