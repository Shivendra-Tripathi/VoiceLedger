package io.github.trip.shiv.vcledger.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a person whose credits/debits are tracked in a shopkeeper's ledger.
 *
 * NOTE: There is intentionally NO "balance" field here. The outstanding
 * balance is a derived value (sum of CREDIT transactions minus sum of DEBIT
 * transactions) and must be computed on demand from the Transaction history,
 * not stored redundantly, to avoid data-integrity drift between the stored
 * balance and the actual transaction log.
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
// user and transactions excluded from toString to prevent lazy-loading
// triggers and circular reference chains (Customer -> User -> Customers...).
@ToString(exclude = {"user", "transactions"})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * The shopkeeper who owns this customer's ledger entry.
     * EAGER-free (LAZY) fetch keeps loading a Customer cheap; the owning
     * User is only fetched when explicitly navigated to.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Full transaction history for this customer. Cascade ALL + orphan
     * removal keeps transactions consistent with their parent customer
     * (e.g. deleting a customer removes their ledger entries), while the
     * transaction history itself is preserved for as long as the customer
     * exists — nothing here overwrites or summarizes it.
     */
    @Builder.Default
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}