package io.github.trip.shiv.vcledger.business.dtos.ledger.intent;


import io.github.trip.shiv.vcledger.business.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeleteTransactionOperation implements LedgerOperation {
	
	
    private final Long transactionId;

    @Override
    public OperationType getType() {
        return OperationType.DELETE_TRANSACTION;
    }
}