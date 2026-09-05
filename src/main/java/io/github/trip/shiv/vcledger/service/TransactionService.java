package io.github.trip.shiv.vcledger.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.Transaction;
import io.github.trip.shiv.vcledger.entity.TransactionType;
import io.github.trip.shiv.vcledger.repository.TransactionRepository;
import io.github.trip.shiv.vcledger.service.exception.InvalidTransactionException;
import io.github.trip.shiv.vcledger.service.exception.TransactionNotFoundException;

/**
 * Business logic for ledger Transactions.
 *
 * Responsibility split:
 *   TransactionController -> HTTP request/response only, delegates to this service
 *   TransactionService      -> business rules + ownership enforcement (this class)
 *   TransactionRepository    -> persistence access (unmodified, reused as-is)
 *   Transaction              -> JPA entity (unmodified)
 *
 * Ownership model: Transaction has no direct User reference — ownership is
 * indirect, via Transaction.customer.user. Two strategies are used here,
 * matching what the repository already provides:
 *
 *   - For operations scoped to ONE known customer (create, customer
 *     history, balance): ownership is verified by delegating to
 *     CustomerService#getCustomerById, which already throws if the
 *     customer isn't owned by the caller. This avoids re-implementing
 *     that check here.
 *   - For operations addressed by transaction id alone (get/update/delete
 *     by id, get-all): ownership is enforced directly at the query level
 *     via TransactionRepository#findByIdAndCustomer_UserId /
 *     #findByCustomer_UserIdOrderByCreatedAtDesc, so an unowned id is
 *     indistinguishable from a nonexistent one.
 *
 * As with UserService/CustomerService, this class takes a plain String
 * email identifier rather than an Authentication object, so it works
 * equally well called from:
 *   - TransactionController (normal REST flow), or
 *   - a future VoiceController/VoiceService, once NLP has turned speech
 *     into structured (customerId/customerName, amount, type, description)
 *     data. The NLP layer's only job is producing that structured data;
 *     this class remains the single place where transactions are actually
 *     validated and persisted, for both callers.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CustomerService customerService;

    public TransactionService(TransactionRepository transactionRepository,
                                UserService userService,
                                CustomerService customerService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.customerService = customerService;
    }

    /**
     * Create a ledger transaction for a customer owned by the authenticated user.
     *
     * Ownership is enforced by resolving the customer through
     * CustomerService#getCustomerById, which throws CustomerNotFoundException
     * if the customer doesn't exist or belongs to a different user — so a
     * transaction can never be created against another user's customer.
     *
     * type semantics follow the entity's own documentation: CREDIT
     * increases what the customer owes, DEBIT decreases it (a payment).
     *
     * @param currentUserEmail the authenticated user's email
     * @param customerId        the customer this entry belongs to
     * @param amount            must be non-null and > 0
     * @param type              CREDIT or DEBIT
     * @param description       optional free-text note
     * @throws InvalidTransactionException if amount or type fail validation
     */
    @Transactional
    public Transaction createTransaction(String currentUserEmail, Long customerId,
                                          BigDecimal amount, TransactionType type, String description) {
        Customer customer = customerService.getCustomerById(currentUserEmail, customerId);

        validateAmount(amount);
        validateType(type);

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .type(type)
                .description(description)
                .customer(customer)
                .build();

        return transactionRepository.save(transaction);
    }

    /**
     * A single transaction, verified to belong (via its customer) to the
     * authenticated user. Ownership is enforced at the query level.
     *
     * @throws TransactionNotFoundException if no such transaction exists for this user
     */
    @Transactional(readOnly = true)
    public Transaction getTransactionById(String currentUserEmail, Long transactionId) {
        Long userId = userService.getUserByEmail(currentUserEmail).getId();
        return transactionRepository.findByIdAndCustomer_UserId(transactionId, userId)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found with id: " + transactionId));
    }

    /**
     * Every transaction across every customer belonging to the
     * authenticated user, most recent first. Never falls back to
     * transactionRepository.findAll().
     */
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions(String currentUserEmail) {
        Long userId = userService.getUserByEmail(currentUserEmail).getId();
        return transactionRepository.findByCustomer_UserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Full transaction history for one customer, most recent first.
     * Ownership is verified via CustomerService#getCustomerById before the
     * transaction query runs, so a customer id belonging to another user
     * never leaks its history.
     */
    @Transactional(readOnly = true)
    public List<Transaction> getCustomerTransactions(String currentUserEmail, Long customerId) {
        customerService.getCustomerById(currentUserEmail, customerId); // ownership check; result unused
        return transactionRepository.findByCustomer_IdOrderByCreatedAtDesc(customerId);
    }

    /**
     * Update an existing transaction's editable fields.
     *
     * Only amount, type, and description are updatable. The transaction's
     * customer is intentionally never reassigned here — the entity has no
     * setter call for `customer` in this method — so a transaction can
     * never be moved to a different customer (and thus a different user)
     * through an update. Null/blank values are treated as "no change".
     *
     * @throws TransactionNotFoundException if the transaction doesn't exist or isn't owned by this user
     * @throws InvalidTransactionException  if a supplied amount is <= 0
     */
    @Transactional
    public Transaction updateTransaction(String currentUserEmail, Long transactionId,
                                          BigDecimal newAmount, TransactionType newType, String newDescription) {
        Long userId = userService.getUserByEmail(currentUserEmail).getId();
        Transaction transaction = transactionRepository.findByIdAndCustomer_UserId(transactionId, userId)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found with id: " + transactionId));

        if (newAmount != null) {
            validateAmount(newAmount);
            transaction.setAmount(newAmount);
        }
        if (newType != null) {
            transaction.setType(newType);
        }
        if (StringUtils.hasText(newDescription)) {
            transaction.setDescription(newDescription);
        }

        return transactionRepository.save(transaction);
    }

    /**
     * Delete a transaction, after verifying (at the query level) that it
     * belongs to the authenticated user.
     *
     * @throws TransactionNotFoundException if the transaction doesn't exist or isn't owned by this user
     */
    @Transactional
    public void deleteTransaction(String currentUserEmail, Long transactionId) {
        Long userId = userService.getUserByEmail(currentUserEmail).getId();
        Transaction transaction = transactionRepository.findByIdAndCustomer_UserId(transactionId, userId)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found with id: " + transactionId));
        transactionRepository.delete(transaction);
    }

    /**
     * Outstanding balance for one customer: sum of CREDIT minus sum of
     * DEBIT, computed in the database via
     * TransactionRepository#calculateBalanceByCustomerId rather than
     * loading every transaction into memory.
     *
     * Ownership is verified via CustomerService#getCustomerById before the
     * aggregation query runs.
     */
    @Transactional(readOnly = true)
    public BigDecimal getCustomerBalance(String currentUserEmail, Long customerId) {
        customerService.getCustomerById(currentUserEmail, customerId); // ownership check; result unused
        return transactionRepository.calculateBalanceByCustomerId(customerId);
    }

    /**
     * Amount must be present and strictly positive. Direction (increases
     * vs. decreases the balance) is expressed entirely through `type`, not
     * through the sign of the amount — so a negative or zero amount is
     * always invalid, regardless of type.
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidTransactionException("Transaction amount is required");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Transaction amount must be greater than zero");
        }
    }

    private void validateType(TransactionType type) {
        if (type == null) {
            throw new InvalidTransactionException("Transaction type is required");
        }
    }
}