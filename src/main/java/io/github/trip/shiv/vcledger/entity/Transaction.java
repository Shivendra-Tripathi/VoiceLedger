package io.github.trip.shiv.vcledger.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a single ledger entry belonging to a Customer.
 *
 * type = CREDIT -> customer's outstanding amount increases (they owe more)
 * type = DEBIT  -> customer's outstanding amount decreases (they paid)
 *
 * Example:
 *   "Ramesh took goods worth 500" -> CREDIT 500
 *   "Ramesh paid 200"             -> DEBIT 200
 *   Outstanding balance = 500 - 200 = 300
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
// customer excluded from toString to avoid triggering a lazy load and to
// avoid a circular Transaction -> Customer -> Transactions... chain.
@ToString(exclude = "customer")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Monetary value of the transaction. BigDecimal is used instead of
     * double/float because floating point types cannot represent decimal
     * currency values exactly, which leads to silent rounding errors that
     * accumulate across many ledger entries. precision/scale bound the
     * column to values suitable for currency (up to 17 digits, 2 decimals).
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @Column(length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The customer this ledger entry belongs to. LAZY fetch keeps bulk
     * transaction queries cheap; nullable = false enforces that every
     * transaction must belong to exactly one customer.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}