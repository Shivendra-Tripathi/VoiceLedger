package io.github.trip.shiv.vcledger.business.dtos.visualpreviews;

import java.math.BigDecimal;

import io.github.trip.shiv.vcledger.business.dtos.projections.CustomerBalanceProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerBalanceData {

    private PersonInfo customer;
    private BigDecimal balance;
    
    
    public static CustomerBalanceData from(CustomerBalanceProjection projection) {
    	return new CustomerBalanceData(
    			PersonInfo.fromCustomer(projection.getCustomer()),
    			projection.getBalance());
    }
}