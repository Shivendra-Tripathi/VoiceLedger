package io.github.trip.shiv.vcledger.business.dtos.projections;

import java.math.BigDecimal;

import io.github.trip.shiv.vcledger.entity.Customer;

public interface CustomerBalanceProjection {

    Customer getCustomer();

    BigDecimal getBalance();
}