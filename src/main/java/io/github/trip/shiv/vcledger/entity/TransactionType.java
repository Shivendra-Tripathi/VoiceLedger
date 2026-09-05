package io.github.trip.shiv.vcledger.entity;

/**
 * Represents the nature of a ledger entry.
 *
 * CREDIT = customer owes the shopkeeper more (outstanding balance increases)
 *          e.g. "Ramesh took goods worth 500" -> CREDIT 500
 *
 * DEBIT  = customer has paid the shopkeeper (outstanding balance decreases)
 *          e.g. "Ramesh paid 200" -> DEBIT 200
 */
public enum TransactionType {
    CREDIT,
    DEBIT
}