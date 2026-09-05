package io.github.trip.shiv.vcledger.controller.transactionController.dto;



import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.github.trip.shiv.vcledger.entity.Transaction;
import io.github.trip.shiv.vcledger.entity.TransactionType;
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

    
    private LocalDateTime time;

    
    private String description;
    
    public TransactionResponse(Transaction transaction) {
    	amount = transaction.getAmount();
    	transactionType = transaction.getType();
    	time = transaction.getCreatedAt();
    	description = transaction.getDescription();
    }
}