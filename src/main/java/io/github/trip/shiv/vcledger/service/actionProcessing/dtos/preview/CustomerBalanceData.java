package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.preview;

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

    private Long customerId;
    private String customerName;
    private String photoUrl;
    private BigDecimal balance;
}