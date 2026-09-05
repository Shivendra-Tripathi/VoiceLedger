package io.github.trip.shiv.vcledger.repository;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.trip.shiv.vcledger.entity.Transaction;
import io.github.trip.shiv.vcledger.entity.TransactionType;

/**
 * Repository for individual ledger entries.
 *
 * Covers the queries a ledger app needs beyond plain CRUD: pulling a
 * customer's full or filtered history (by type, by date range), fetching
 * the most recent entry, and — most importantly — deriving the customer's
 * outstanding balance directly from the transaction log, since balance is
 * intentionally not stored anywhere (see Customer entity).
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /** Full transaction history for a customer, most recent first. */
    List<Transaction> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);
    Page<Transaction> findByCustomer_IdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    

    /** History filtered by entry type (e.g. "show me all payments from Ramesh"). */
    List<Transaction> findByCustomer_IdAndTypeOrderByCreatedAtDesc(Long customerId, TransactionType type);
    Page<Transaction> findByCustomer_IdAndTypeOrderByCreatedAtDesc(Long customerId, TransactionType type,Pageable pageable);
    
    
    /** History within a date range, for statements/reports. */
    List<Transaction> findByCustomer_IdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long customerId, LocalDateTime start, LocalDateTime end);
    Page<Transaction> findByCustomer_IdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long customerId, LocalDateTime start, LocalDateTime end,Pageable pageable);
    

    /** Ownership-checked single lookup, scoped through the customer's owning user. */
    Optional<Transaction> findByIdAndCustomer_UserId(Long id, Long userId);

    /** Most recent transaction for a customer — e.g. "undo my last entry". */
    Optional<Transaction> findFirstByCustomer_IdOrderByCreatedAtDesc(Long customerId);

    /** All transactions across every customer belonging to a given shopkeeper. */
    List<Transaction> findByCustomer_UserIdOrderByCreatedAtDesc(Long userId);
    Page<Transaction> findByCustomer_UserIdOrderByCreatedAtDesc(Long userId,Pageable pageable);

    /**
     * Derives the current outstanding balance for one customer directly
     * from the transaction log: sum of CREDIT minus sum of DEBIT.
     * COALESCE guards against a customer with zero transactions returning
     * null instead of 0.
     */
    @Query("""
            SELECT COALESCE(SUM(
                CASE WHEN t.type = io.github.trip.shiv.vcledger.entity.TransactionType.CREDIT
                     THEN t.amount ELSE -t.amount END), 0)
            FROM Transaction t
            WHERE t.customer.id = :customerId
            """)
    BigDecimal calculateBalanceByCustomerId(@Param("customerId") Long customerId);

    /** Sum of all amounts of one type for a customer, e.g. total ever credited. */
    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.customer.id = :customerId AND t.type = :type
            """)
    BigDecimal sumAmountByCustomerIdAndType(
            @Param("customerId") Long customerId, @Param("type") TransactionType type);
}