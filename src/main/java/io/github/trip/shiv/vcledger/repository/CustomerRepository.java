package io.github.trip.shiv.vcledger.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.trip.shiv.vcledger.entity.Customer;

/**
 * Repository for Customer records.
 *
 * The methods here are shaped around two future needs in particular:
 *
 * 1. Ownership-scoped access — every read/write in a multi-tenant ledger
 *    must be scoped to the requesting User, so most lookups take a userId
 *    alongside the customer identifier/name to prevent one shopkeeper from
 *    ever touching another's customers.
 *
 * 2. Voice/NLP name resolution — a spoken command like "Ramesh paid 200"
 *    only gives a name, not an id, so fuzzy/case-insensitive name lookups
 *    scoped to the current user are essential for turning a transcribed
 *    name into a Customer row.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /** All customers belonging to a given shopkeeper. */
    List<Customer> findByUser_Id(Long userId);

    /**
     * Ownership-checked single lookup — use this instead of plain
     * findById() whenever a request is scoped to the logged-in user, so a
     * customer id belonging to a different user simply isn't found.
     */
    Optional<Customer> findByIdAndUser_Id(Long id, Long userId);

    /**
     * Exact (case-insensitive) name match within one user's customer list.
     * Useful when the NLP parser extracts a clean name and a precise match
     * is expected/preferred.
     */
    Optional<Customer> findByUser_IdAndNameIgnoreCase(Long userId, String name);

    /**
     * Fuzzy name search within one user's customer list, for voice
     * commands where the transcribed name may be partial or slightly off
     * (e.g. matching "Ram" against "Ramesh Kumar").
     */
    List<Customer> findByUser_IdAndNameContainingIgnoreCase(Long userId, String namePart);

    /** Quick existence check, e.g. before creating a duplicate customer. */
    boolean existsByUser_IdAndNameIgnoreCase(Long userId, String name);

    /** Lookup by phone number, scoped to the owning user. */
    Optional<Customer> findByUser_IdAndPhone(Long userId, String phone);
}