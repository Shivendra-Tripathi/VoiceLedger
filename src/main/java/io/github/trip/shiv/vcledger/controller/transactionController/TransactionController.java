package io.github.trip.shiv.vcledger.controller.transactionController;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.trip.shiv.vcledger.controller.general.dto.MessageResponse;
import io.github.trip.shiv.vcledger.controller.transactionController.dto.CreateTransactionRequest;
import io.github.trip.shiv.vcledger.controller.transactionController.dto.TransactionResponse;
import io.github.trip.shiv.vcledger.controller.transactionController.dto.UpdateTransactionRequest;
import io.github.trip.shiv.vcledger.entity.Transaction;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.security.SecurityUtils;
import io.github.trip.shiv.vcledger.service.TransactionService;
import jakarta.validation.Valid;

/**
 * TransactionController
 *
 * Skeleton REST controller for ledger transaction management.
 * NOTE: No business logic, service calls, or repository calls are implemented here.
 * All endpoints return dummy responses for API-contract testing purposes only.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	
	
	SecurityUtils securityUtils;
	TransactionService transactionService;
	
	
	public TransactionController(
			SecurityUtils utils,
			TransactionService transactionService) {
		this.securityUtils =utils;
		this.transactionService = transactionService;
	}
	
	
    /**
     * POST /api/transactions
     * Create a ledger transaction.
     * Requires authentication (JWT).
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
    		@Valid @RequestBody CreateTransactionRequest request) {
        User user = securityUtils.getAuthenticatedUser();
        
        Transaction transaction = 
        		transactionService.createTransaction(user.getEmail(), request.getCustomerId(),
        		request.getAmount(), request.getType(), request.getDescription());
        
        return ResponseEntity
        		.status(HttpStatus.CREATED)
        		.body(new TransactionResponse(transaction));
    }

    /**
     * GET /api/transactions
     * Get all transactions of the authenticated user.
     * Requires authentication (JWT).
     */
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        User user = securityUtils.getAuthenticatedUser();
        List<TransactionResponse> response = 
        		transactionService.getAllTransactions(user.getEmail())
        		.stream()
        		.map(transaction->new TransactionResponse(transaction))
        		.toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/transactions/{id}
     * Get a particular transaction.
     * Requires authentication (JWT).
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
    	User user = securityUtils.getAuthenticatedUser();
    	Transaction transaction = transactionService.getTransactionById(user.getEmail(), id);
    	return ResponseEntity.ok(new TransactionResponse(transaction));
    }

    /**
     * PUT /api/transactions/{id}
     * Update a transaction.
     * Requires authentication (JWT).
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable Long id,
    		@Valid @RequestBody UpdateTransactionRequest request){
       User user = securityUtils.getAuthenticatedUser();
    
	   Transaction transaction = transactionService.updateTransaction(
			   user.getEmail(), id, request.getAmount(), request.getType(), request.getDescription());
	   
	    return ResponseEntity
	    		.ok(new TransactionResponse(transaction));
    }

    /**
     * DELETE /api/transactions/{id}
     * Delete a transaction.
     * Requires authentication (JWT).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteTransaction(@PathVariable Long id) {
        
    	User user = securityUtils.getAuthenticatedUser();
        
    	transactionService.deleteTransaction(user.getEmail(), id);
     
    	return ResponseEntity.ok(new MessageResponse("Successfully Deleted the Transaction"));
    }
}