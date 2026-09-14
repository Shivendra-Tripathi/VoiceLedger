package io.github.trip.shiv.vcledger.business.dtos.transactioncontroller.res;



import java.math.BigDecimal;
import java.time.Instant;

import io.github.trip.shiv.vcledger.business.enums.TransactionType;
import io.github.trip.shiv.vcledger.entity.Transaction;
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
public class TransactionResponse {

   
    private BigDecimal amount;

 
    private TransactionType transactionType;

    
    private Instant time;

    
    private String description;
    
    public TransactionResponse(Transaction transaction) {
    	amount = transaction.getAmount();
    	transactionType = transaction.getType();
    	time = transaction.getCreatedAt();
    	description = transaction.getDescription();
    }
}