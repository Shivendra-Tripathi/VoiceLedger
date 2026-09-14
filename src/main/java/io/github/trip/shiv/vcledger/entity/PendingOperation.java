package io.github.trip.shiv.vcledger.entity;


import java.time.Instant;

import org.springframework.security.core.Transient;

import io.github.trip.shiv.vcledger.business.enums.OperationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "pending_operations",
    indexes = {
        @Index(
            name = "idx_pending_operation_operation_id",
            columnList = "operation_id",
            unique = true
        ),
        @Index(
            name = "idx_pending_operation_shopkeeper",
            columnList = "shopkeeper_id"
        ),
        @Index(
            name = "idx_pending_operation_status",
            columnList = "status"
        ),
        @Index(
            name = "idx_pending_operation_expires_at",
            columnList = "expires_at"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingOperation {
	
	 
	 
	 @Transient
	 public enum Status {

		    PENDING,

		    EXECUTED,

		    CANCELLED,

		    EXPIRED
		}
	 
	 
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * Public identifier returned to the frontend.
     *
     * Example:
     * "OP-7f3a91c2"
     */
    @Column(
        name = "operation_id",
        nullable = false,
        unique = true,
        length = 100
    )
    private String operationId;


    /**
     * The shopkeeper/user who created this pending operation.
     *
     * This is important because a user must not be able
     * to confirm another user's pending operation.
     */
    @Column(
        name = "shopkeeper_id",
        nullable = false
    )
    private Long shopkeeperId;


    /**
     * What operation is waiting for confirmation.
     */
    @Enumerated(EnumType.STRING)
    @Column(
        name = "operation_type",
        nullable = false,
        length = 50
    )
    private OperationType operationType;


    /**
     * Complete server-side data required to execute
     * the operation.
     *
     * This should NOT be trusted from the frontend.
     *
     * Example:
     * {
     *   "customerId": 42,
     *   "amount": 500,
     *   "transactionType": "CREDIT"
     * }
     */
    @Column(
        name = "payload",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String payload;



    /**
     * Time after which this operation can no longer
     * be confirmed.
     */
    @Column(
        name = "expires_at",
        nullable = false
    )
    private Instant expiresAt;


    /**
     * Current lifecycle state of the operation.
     */
    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 30
    )
    @Builder.Default
    private Status status =
            Status.PENDING;


    /**
     * When this pending operation was created.
     */
    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    @Builder.Default
    private Instant createdAt = Instant.now();


    /**
     * When the operation was actually executed.
     *
     * NULL while the operation is still pending.
     */
    @Column(
        name = "executed_at"
    )
    private Instant executedAt;


    /**
     * Optional timestamp for cancellation.
     */
    @Column(
        name = "cancelled_at"
    )
    private Instant cancelledAt;
}