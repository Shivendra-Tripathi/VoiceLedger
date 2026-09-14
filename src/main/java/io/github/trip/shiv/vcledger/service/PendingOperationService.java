package io.github.trip.shiv.vcledger.service;


import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.trip.shiv.vcledger.business.dtos.ledger.intent.LedgerOperation;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.business.exceptions.PendingOperationNotFoundException;
import io.github.trip.shiv.vcledger.business.factories.LedgerOperationFactory;
import io.github.trip.shiv.vcledger.business.factories.PendingOperationFactory;
import io.github.trip.shiv.vcledger.business.processors.interfaces.LedgerOperationExecutorDelegator;
import io.github.trip.shiv.vcledger.entity.PendingOperation;
import io.github.trip.shiv.vcledger.entity.PendingOperation.Status;
import io.github.trip.shiv.vcledger.repository.PendingOperationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PendingOperationService {

    private final PendingOperationRepository pendingOperationRepository;

    private final PendingOperationFactory pendingOperationFactory;

    private final LedgerOperationFactory ledgerOperationFactory;
    
    private final LedgerOperationExecutorDelegator ledgerOperationExecutorDelegator;

   


    /**
     * Creates and persists a new pending operation.
     *
     * The operation is converted into a PendingOperation,
     * assigned a public operation ID and stored in the database.
     */
    @Transactional
    public PendingOperation create(
            Long shopkeeperId,
            LedgerOperation operation
    ) {

        if (shopkeeperId == null) {
            throw new IllegalArgumentException(
                    "Shopkeeper ID cannot be null"
            );
        }

        if (operation == null) {
            throw new IllegalArgumentException(
                    "Ledger operation cannot be null"
            );
        }

        PendingOperation pendingOperation =
                pendingOperationFactory.fromLedgerOperation(
                		operation , 
                		Instant.now() , 
                		Instant.now().plus(5, ChronoUnit.MINUTES));

        pendingOperation.setOperationId(
                generateOperationId()
        );

        pendingOperation.setShopkeeperId(shopkeeperId);

        return pendingOperationRepository.save(pendingOperation);
    }


    /**
     * Returns a pending operation belonging to the given shopkeeper.
     */
    @Transactional(readOnly = true)
    public PendingOperation getById(
            Long operationId,
            Long shopkeeperId
    ) {

        return pendingOperationRepository
                .findByIdAndShopkeeperId(
                        operationId,
                        shopkeeperId
                )
                .orElseThrow(() ->
                        new PendingOperationNotFoundException(
                                "Pending operation not found"
                        )
                );
    }


    /**
     * Returns all pending operations belonging
     * to the given shopkeeper.
     */
    @Transactional(readOnly = true)
    public List<PendingOperation> getPendingOperations(
            Long shopkeeperId
    ) {

        return pendingOperationRepository
                .findByShopkeeperIdAndStatus(
                        shopkeeperId,
                        Status.PENDING
                );
    }


    /**
     * Confirms and executes a pending operation.
     *
     * Flow:
     *
     * PendingOperation
     *       ↓
     * validate
     *       ↓
     * LedgerOperationFactory
     *       ↓
     * LedgerOperation
     *       ↓
     * LedgerOperationProcessorService
     *       ↓
     * mark as EXECUTED
     */
    @Transactional
    public LedgerResponse confirm(
            String operationId,
            Long shopkeeperId
    ) {

        PendingOperation pendingOperation =
                getByOperationId(
                        operationId,
                        shopkeeperId
                );

        validateForExecution(pendingOperation);
        
        LedgerOperation operation = ledgerOperationFactory.fromPendingOperation(pendingOperation);

        return ledgerOperationExecutorDelegator.delegate(operation);
    }


    /**
     * Cancels a pending operation.
     */
    @Transactional
    public void cancel(
            String operationId,
            Long shopkeeperId
    ) {

        PendingOperation pendingOperation =
                getByOperationId(
                        operationId,
                        shopkeeperId
                );

        if (pendingOperation.getStatus() != Status.PENDING) {
            throw new IllegalStateException(
                    "Only pending operations can be cancelled"
            );
        }

        pendingOperation.setStatus(Status.CANCELLED);
        pendingOperation.setCancelledAt(Instant.now());

        pendingOperationRepository.save(pendingOperation);
    }


    /**
     * Finds a pending operation using its public operation ID
     * while also enforcing shopkeeper ownership.
     */
    private PendingOperation getByOperationId(
            String operationId,
            Long shopkeeperId
    ) {

        return pendingOperationRepository
                .findByOperationIdAndShopkeeperId(
                        operationId,
                        shopkeeperId
                )
                .orElseThrow(() ->
                        new PendingOperationNotFoundException(
                                "Pending operation not found"
                        )
                );
    }


    /**
     * Ensures that an operation is still eligible for execution.
     */
    private void validateForExecution(
            PendingOperation pendingOperation
    ) {

        if (pendingOperation.getStatus() != Status.PENDING) {
            throw new IllegalStateException(
                    "Pending operation has already been processed"
            );
        }

        if (pendingOperation.getExpiresAt() != null
                && pendingOperation.getExpiresAt()
                        .isBefore(Instant.now())) {

            pendingOperation.setStatus(Status.EXPIRED);

            pendingOperationRepository.save(
                    pendingOperation
            );

            throw new IllegalStateException(
                    "Pending operation has expired"
            );
        }
    }


    /**
     * Generates the public identifier exposed to the frontend.
     */
    private String generateOperationId() {

        return "OP-" + UUID.randomUUID();
    }
}
