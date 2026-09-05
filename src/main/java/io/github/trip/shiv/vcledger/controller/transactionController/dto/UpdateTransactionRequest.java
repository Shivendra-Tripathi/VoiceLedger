package io.github.trip.shiv.vcledger.controller.transactionController.dto;


import java.math.BigDecimal;

import io.github.trip.shiv.vcledger.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class UpdateTransactionRequest {
	
	@NotNull(message = "Customer ID is required")
	private Long customerId;
	
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private TransactionType type;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}

