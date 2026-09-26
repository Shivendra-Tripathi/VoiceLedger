package io.github.trip.shiv.vcledger.controller;


import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.CustomerBalanceData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.github.trip.shiv.vcledger.core.dtos.general.res.MessageResponse;
import io.github.trip.shiv.vcledger.core.dtos.transactioncontroller.req.CreateTransactionRequest;
import io.github.trip.shiv.vcledger.core.dtos.transactioncontroller.req.UpdateTransactionRequest;
import io.github.trip.shiv.vcledger.core.dtos.transactioncontroller.res.TransactionResponse;
import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.PersonInfo;
import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.TransactionData;
import io.github.trip.shiv.vcledger.core.enums.MoneyDirection;
import io.github.trip.shiv.vcledger.core.utilities.SecurityUtils;
import io.github.trip.shiv.vcledger.entity.Transaction;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.service.TransactionService;
import jakarta.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

	
	
	private final SecurityUtils securityUtils;
	private final TransactionService transactionService;
	
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
        		transactionService.createTransaction(user.getId(), request.getCustomerId(),
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
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
    		@PageableDefault( 
    				size = 12,
    			    sort = "createdAt",
    			    direction = Sort.Direction.DESC) 
    		Pageable pageable) {
        User user = securityUtils.getAuthenticatedUser();
        Page<TransactionResponse> response = 
        		transactionService.getTransactions(user.getId(),pageable)
        		.map(transaction -> new TransactionResponse(transaction))
        		;
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/transactions/{id}
     * Get a particular transaction.
     * Requires authentication (JWT).
	 * USEFUL TO FETCH A SINGLE TRANSACTION RELATED TO CERTAIN CUSTOMER.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
    	User user = securityUtils.getAuthenticatedUser();
    	Transaction transaction = transactionService.getTransactionById(user.getId(), id);
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
			   user.getId(), id, request.getAmount(), request.getType(), request.getDescription());
	   
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
        
    	transactionService.deleteTransaction(user.getId(), id);
     
    	return ResponseEntity.ok(new MessageResponse("Successfully Deleted the Transaction"));
    }
    
    
    
    
    
    
    /**
     * GET /api/transactions/data
     * Returns the Visual Representation of the Transaction Data
	 * USEFUL TO FETCH ALL THE TRANSACTIONS RELATED TO A USER/SHOPKEEPER.
     */
    @GetMapping("/data")
    public ResponseEntity<Page<TransactionData>> getAllTransactionsData(
    		@PageableDefault( 
    				size = 12,
    			    sort = "createdAt",
    			    direction = Sort.Direction.DESC) 
    		Pageable pageable){
    	
    	User user = securityUtils.getAuthenticatedUser();
        Page<Transaction> transactions = 
        		transactionService.getTransactions(user.getId(),pageable)
        		;
        
        //Now convert the transaction into TransactionData
        
        Page<TransactionData> transactionData =
        		transactions
        		.map(transaction -> TransactionData.builder()
        				.shopkeeper(PersonInfo.fromShopkeeper(user))
        				.customer(PersonInfo.fromCustomer(transaction.getCustomer()))
        				.createdAt(transaction.getCreatedAt())
        				.moneyDirection(MoneyDirection.from(transaction.getType()))
        				.build()
        				);
        
        return ResponseEntity.ok(
        		transactionData);
    }

	@GetMapping("/customers/{customerId}")
	public ResponseEntity<Page<TransactionResponse>> getCustomerTransactionHistory(
			@PathVariable Long customerId,
			@PageableDefault(
					page = 0,
					size = 10,
					sort = "createdAt",
					direction = Sort.Direction.DESC
			)
			Pageable pageable) {

		User user = securityUtils.getAuthenticatedUser();

		Page<TransactionResponse> responses = transactionService
				.getCustomerTransactions(user.getId(), customerId, pageable)
				.map(TransactionResponse::new);

		return ResponseEntity.ok(responses);
	}




}