package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.preview;

import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationPreview {

    private OperationType operationType;
    private String description;
    private TransactionPreview transaction;
    private PersonInfo customer;
}