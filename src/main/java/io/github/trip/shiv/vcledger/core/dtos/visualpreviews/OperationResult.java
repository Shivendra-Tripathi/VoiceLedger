package io.github.trip.shiv.vcledger.core.dtos.visualpreviews;

import io.github.trip.shiv.vcledger.core.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationResult {

    private OperationType operationType;

    private TransactionData transaction;

}