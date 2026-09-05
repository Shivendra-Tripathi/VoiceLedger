package io.github.trip.shiv.vcledger.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * Represents the shopkeeper/owner who uses the ledger application.
 * A User owns many Customers, each of whom has their own transaction history.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Only id is used for equality/hash so entities behave correctly in Sets/Maps
// and across JPA proxy (de)serialization.
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
// customers collection excluded to avoid huge/circular toString output.
@ToString(exclude = "customers")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    @Column(nullable = false, unique = true, length = 150)
    private String password;


    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * All customers registered under this shopkeeper's ledger.
     * Cascade ALL + orphanRemoval ensures that deleting a User also cleans up
     * their customers (and transitively, transactions), which is the correct
     * behavior for a ledger scoped entirely to its owning shopkeeper.
     * LAZY fetch avoids pulling the whole customer graph on every User load.
     */
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Customer> customers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}