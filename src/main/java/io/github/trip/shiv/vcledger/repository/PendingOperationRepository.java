package io.github.trip.shiv.vcledger.repository;



import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.trip.shiv.vcledger.entity.PendingOperation;


@Repository
public interface PendingOperationRepository
        extends JpaRepository<PendingOperation, Long> {

    /*
     * Find an operation using the public operation ID.
     *
     * This will be the primary query during confirmation.
     */
    Optional<PendingOperation> findByOperationId(String operationId);


    /*
     * Check whether an operation exists.
     */
    boolean existsByOperationId(String operationId);


    /*
     * Find an operation belonging to a particular shopkeeper.
     *
     * Useful for ownership validation.
     */
    Optional<PendingOperation> findByOperationIdAndShopkeeperId(
            String operationId,
            Long shopkeeperId
    );


    /*
     * Find only a PENDING operation.
     *
     * Useful when confirming an operation.
     */
    Optional<PendingOperation> findByOperationIdAndStatus(
            String operationId,
            PendingOperation.Status status
    );


    /*
     * Most useful query for confirmation:
     *
     * operation must belong to the shopkeeper
     * AND still be PENDING.
     */
    Optional<PendingOperation> findByOperationIdAndShopkeeperIdAndStatus(
            String operationId,
            Long shopkeeperId,
            PendingOperation.Status status
    );


    /*
     * Find all pending operations of a particular shopkeeper.
     */
    List<PendingOperation> findByShopkeeperIdAndStatus(
            Long shopkeeperId,
            PendingOperation.Status status
    );


    /*
     * Find expired pending operations.
     *
     * Useful for a scheduled cleanup job.
     */
    List<PendingOperation> findByStatusAndExpiresAtBefore(
    		PendingOperation.Status status,
            Instant time
    );


    /*
     * Atomically mark an operation as EXECUTED.
     *
     * The WHERE status = PENDING condition prevents
     * two simultaneous confirmation requests from
     * executing the same operation.
     */
    @Modifying
    @Query("""
        UPDATE PendingOperation p
        SET p.status = :executedStatus,
            p.executedAt = :executedAt
        WHERE p.operationId = :operationId
          AND p.status = :pendingStatus
    """)
    int markAsExecuted(
            @Param("operationId") String operationId,
            @Param("pendingStatus") PendingOperation.Status pendingStatus,
            @Param("executedStatus") PendingOperation.Status executedStatus,
            @Param("executedAt") Instant executedAt
    );


    /*
     * Atomically cancel a pending operation.
     */
    @Modifying
    @Query("""
        UPDATE PendingOperation p
        SET p.status = :cancelledStatus,
            p.cancelledAt = :cancelledAt
        WHERE p.operationId = :operationId
          AND p.status = :pendingStatus
    """)
    int markAsCancelled(
            @Param("operationId") String operationId,
            @Param("pendingStatus") PendingOperation.Status pendingStatus,
            @Param("cancelledStatus") PendingOperation.Status cancelledStatus,
            @Param("cancelledAt") Instant cancelledAt
    );


    /*
     * Mark expired operations as EXPIRED.
     *
     * This can be called by a scheduled task.
     */
    @Modifying
    @Query("""
        UPDATE PendingOperation p
        SET p.status = :expiredStatus
        WHERE p.status = :pendingStatus
          AND p.expiresAt < :currentTime
    """)
    int markExpiredOperations(
            @Param("pendingStatus") PendingOperation.Status pendingStatus,
            @Param("expiredStatus") PendingOperation.Status expiredStatus,
            @Param("currentTime") Instant currentTime
    );
    
    
    
    Optional<PendingOperation> findByIdAndShopkeeperId(
            Long id,
            Long shopkeeperId
    );

 
}