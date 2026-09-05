package io.github.trip.shiv.vcledger.controller.customercontroller;



import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import io.github.trip.shiv.vcledger.controller.customercontroller.dto.BalanceResponse;
import io.github.trip.shiv.vcledger.controller.customercontroller.dto.CreateCustomerRequest;
import io.github.trip.shiv.vcledger.controller.customercontroller.dto.CustomerResponse;
import io.github.trip.shiv.vcledger.controller.customercontroller.dto.UpdateCustomerRequest;
import io.github.trip.shiv.vcledger.controller.general.dto.MessageResponse;
import io.github.trip.shiv.vcledger.controller.transactionController.dto.TransactionResponse;
import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.security.SecurityUtils;
import io.github.trip.shiv.vcledger.service.CustomerService;
import io.github.trip.shiv.vcledger.service.TransactionService;
import jakarta.validation.Valid;

/**
 * CustomerController
 *
 * Skeleton REST controller for customer management.
 * NOTE: No business logic, service calls, or repository calls are implemented here.
 * All endpoints return dummy responses for API-contract testing purposes only.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	
	
	private final SecurityUtils securityUtils;
	private final CustomerService customerService ;
	private final TransactionService transactionService;
	
	
	public CustomerController(SecurityUtils securityUtils,
			CustomerService customerService,
			TransactionService transactionService) {
		this.securityUtils = securityUtils;
		this.customerService = customerService;
		this.transactionService = transactionService;
	}
	
	
	
    /**
     * POST /api/customers
     * Create a customer.
     * Requires authentication (JWT).
     */
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
       User user = securityUtils.getAuthenticatedUser();
       Customer customer = customerService.createCustomer(user.getEmail(),request.getName(), request.getPhone());
       
       return ResponseEntity.status(HttpStatus.CREATED)
    	        .body(new CustomerResponse(customer));
    }

    /**
     * GET /api/customers
     * Get all customers belonging to the authenticated user.
     * Requires authentication (JWT).
     */
    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(
    		@PageableDefault(
    				page=0,
    				size=10,
    				sort="createdAt",
					direction = Sort.Direction.DESC)
    		Pageable pageable) {
    	User user = securityUtils.getAuthenticatedUser();
    	
       Page<CustomerResponse> customers = 
    		   customerService.getCustomers(user.getEmail(),pageable)
    		   .map(customer -> new CustomerResponse(customer))
    		   ;
       return ResponseEntity.ok(customers);
    }

    /**
     * GET /api/customers/{id}
     * Get a particular customer.
     * Requires authentication (JWT).
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        User user = securityUtils.getAuthenticatedUser();
    	Customer customer = customerService.getCustomerById(user.getEmail(), id);
    	return ResponseEntity.ok(new CustomerResponse(customer));
    }

    /**
     * PUT /api/customers/{id}
     * Update customer information.
     * Requires authentication (JWT).
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateCustomerRequest request) {
        
    	User user = securityUtils.getAuthenticatedUser();
    	Customer customer = customerService.updateCustomer(user.getEmail(), id, request.getNewName(), request.getNewPhone());
    	return ResponseEntity
    			.ok(new CustomerResponse(customer));
    }

    /**
     * DELETE /api/customers/{id}
     * Delete a customer.
     * Requires authentication (JWT).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteCustomer(@PathVariable Long id) {
    	User user = securityUtils.getAuthenticatedUser();
    	customerService.deleteCustomer(user.getEmail(), id);
    	return ResponseEntity.ok(new MessageResponse("Successfully Deleted Customer."));
    }

    /**
     * GET /api/customers/search?name=rahul
     * Search customers by name.
     * Returns all the Customers whose part of name matches with the Query,
     * Requires authentication (JWT).
     */
    @GetMapping("/search/{name}")
    public ResponseEntity<Page<CustomerResponse>> searchCustomers(@PathVariable String name,
    		@PageableDefault(
    				page=0,
    				size=10,
    				sort="createdAt",
					direction = Sort.Direction.DESC)
    		Pageable pageable) {
    	User user = securityUtils.getAuthenticatedUser();
    	Page<CustomerResponse> customerResponses = 
    			customerService.searchCustomers(user.getEmail(), name,pageable)
    			.map(customer->new CustomerResponse(customer))
    			;
    	return ResponseEntity.ok(customerResponses);
    }

    /**
     * GET /api/customers/{customerId}/transactions
     * Get transaction history of a particular customer.
     * Placed here (rather than a separate controller) to avoid duplication.
     * Requires authentication (JWT).
     */
    @GetMapping("/{customerId}/transactions")
    public ResponseEntity<Page<TransactionResponse>> getCustomerTransactionHistory(@PathVariable Long customerId,
    		@PageableDefault(
    				page=0,
    				size=10,
    				sort="createdAt",
					direction = Sort.Direction.DESC)
    		Pageable pageable) {
   
    	User user = securityUtils.getAuthenticatedUser();
    	Page<TransactionResponse> responses = 
    			transactionService.getCustomerTransactions(user.getEmail(), customerId,pageable)
    			.map(transaction->new TransactionResponse(transaction))
    			;
    	return ResponseEntity.ok(responses);
    	
    }

    /**
     * GET /api/customers/{customerId}/balance
     * Get the current balance for a customer.
     * Requires authentication (JWT).
     */
    @GetMapping("/{customerId}/balance")
    public ResponseEntity<BalanceResponse> getCustomerBalance(@PathVariable Long customerId) {
        User user = securityUtils.getAuthenticatedUser();
        BigDecimal balance= transactionService.getCustomerBalance(user.getEmail(), customerId);
        return ResponseEntity.ok(new BalanceResponse(balance));
    }
}