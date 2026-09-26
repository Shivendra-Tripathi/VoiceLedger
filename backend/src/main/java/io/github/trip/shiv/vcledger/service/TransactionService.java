package io.github.trip.shiv.vcledger.service;

import java.math.BigDecimal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.github.trip.shiv.vcledger.core.enums.TransactionType;
import io.github.trip.shiv.vcledger.core.exceptions.custom.business.InvalidTransactionException;
import io.github.trip.shiv.vcledger.core.exceptions.custom.business.TransactionNotFoundException;
import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.Transaction;
import io.github.trip.shiv.vcledger.repository.TransactionRepository;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerService customerService;


    
    @Transactional
    public Transaction createTransaction(Long userId, Long customerId,
                                          BigDecimal amount, TransactionType type, String description) {
    	
        Customer customer = customerService.getCustomerById(userId, customerId);

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
    public Transaction getTransactionById(Long userId, Long transactionId) {
        return transactionRepository.findByIdAndCustomer_UserId(transactionId, userId)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction not found with id: " + transactionId));
    }

    
    
    @Transactional(readOnly = true)
    public Page<Transaction> getTransactions(Long userId,Pageable pageable){
    	return transactionRepository.findByCustomer_UserIdOrderByCreatedAtDesc(userId, pageable);
    }
    
    
    
    @Transactional(readOnly = true)
    public Page<Transaction> getCustomerTransactions(Long userId, Long customerId, Pageable pageable) {
        customerService.getCustomerById(userId, customerId); // ownership check; result unused
        return transactionRepository.findByCustomer_IdOrderByCreatedAtDesc(customerId,pageable);
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
    public Transaction updateTransaction(Long userId, Long transactionId,
                                          BigDecimal newAmount, TransactionType newType, String newDescription) {
     
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
    public void deleteTransaction(Long userId, Long transactionId) {
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
    public BigDecimal getCustomerBalance(Long userId, Long customerId) {
        customerService.getCustomerById(userId, customerId); // ownership check; result unused
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