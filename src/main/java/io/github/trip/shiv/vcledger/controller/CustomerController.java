package io.github.trip.shiv.vcledger.controller;



import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.trip.shiv.vcledger.business.dtos.customercontroller.req.CreateCustomerRequest;
import io.github.trip.shiv.vcledger.business.dtos.customercontroller.req.UpdateCustomerRequest;
import io.github.trip.shiv.vcledger.business.dtos.general.res.MessageResponse;
import io.github.trip.shiv.vcledger.business.dtos.transactioncontroller.res.TransactionResponse;
import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.CustomerBalanceData;
import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.PersonInfo;
import io.github.trip.shiv.vcledger.business.utilities.SecurityUtils;
import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.User;
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
	@PostMapping(
		    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
		)
	public ResponseEntity<CustomerBalanceData> createCustomer(
		        @Valid @RequestPart("customer") CreateCustomerRequest request,
		        @RequestPart(value = "image", required = false) MultipartFile image) {
		
       User user = securityUtils.getAuthenticatedUser();
       Customer customer = customerService.createCustomer(user.getId(),request.getName(), request.getPhone(),image);
       
       CustomerBalanceData response = 
    		   CustomerBalanceData.builder()
    		   .balance(BigDecimal.ZERO)
    		   .customer(PersonInfo.fromCustomer(customer))
    		   .build();
    		         
       return ResponseEntity.status(HttpStatus.CREATED)
    	        .body(response);
    }

    /**
     * GET /api/customers
     * Get all customers belonging to the authenticated user.
     * Requires authentication (JWT).
     */
    @GetMapping
    public ResponseEntity<Page<PersonInfo>> getAllCustomers(
    		@PageableDefault(
    				page=0,
    				size=10,
    				sort="createdAt",
					direction = Sort.Direction.ASC)
    		Pageable pageable) {
    	User user = securityUtils.getAuthenticatedUser();
    	
        Page<PersonInfo> personInfos =
        		customerService.getCustomers(user.getId(), pageable)
    			.map(customer -> PersonInfo.fromCustomer(customer));
        
    	return  ResponseEntity.ok(personInfos);		   
    }

    /**
     * GET /api/customers/{id}
     * Get a particular customer.
     * Requires authentication (JWT).
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonInfo> getCustomerById(@PathVariable Long id) {
        User user = securityUtils.getAuthenticatedUser();
    	Customer customer = customerService.getCustomerById(user.getId(), id);
    	return ResponseEntity.ok(PersonInfo.fromCustomer(customer));
    }

    /**
     * PUT /api/customers/{id}
     * Update customer information.
     * Requires authentication (JWT).
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonInfo> updateCustomer(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateCustomerRequest request) {
        
    	User user = securityUtils.getAuthenticatedUser();
    	Customer customer = customerService.updateCustomer(user.getId(), id, request.getNewName(), request.getNewPhone());
    	return ResponseEntity
    			.ok(PersonInfo.fromCustomer(customer));
    }

    /**
     * DELETE /api/customers/{id}
     * Delete a customer.
     * Requires authentication (JWT).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteCustomer(@PathVariable Long id) {
    	User user = securityUtils.getAuthenticatedUser();
    	customerService.deleteCustomer(user.getId(), id);
    	return ResponseEntity.ok(new MessageResponse("Successfully Deleted Customer."));
    }

    /**
     * GET /api/customers/search?name=rahul
     * Search customers by name.
     * Returns all the Customers whose part of name matches with the Query,
     * Requires authentication (JWT).
     */
    @GetMapping("/search/{name}")
    public ResponseEntity<Page<PersonInfo>> searchCustomers(@PathVariable String name,
    		@PageableDefault(
    				page=0,
    				size=10,
    				sort="createdAt",
					direction = Sort.Direction.DESC)
    		Pageable pageable) {
    	User user = securityUtils.getAuthenticatedUser();
    	Page<PersonInfo> customerResponses = 
    			customerService.searchCustomers(user.getId(), name,pageable)
    			.map(customer->PersonInfo.fromCustomer(customer))
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
    			transactionService.getCustomerTransactions(user.getId(), customerId,pageable)
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
    public ResponseEntity<CustomerBalanceData> getCustomerBalance(@PathVariable Long customerId) {
        User user = securityUtils.getAuthenticatedUser();
        Customer customer = customerService.getCustomerById(user.getId(), customerId);
        BigDecimal balance= transactionService.getCustomerBalance(user.getId(), customerId);
        
        CustomerBalanceData response = 
        		CustomerBalanceData.builder()
        		.balance(balance)
        		.customer(PersonInfo.fromCustomer(customer))
        		.build();
        
        return ResponseEntity.ok(response);
    }
    
    
    /**
     * GET /api/customers/balance
     * Get all customers belonging to the authenticated user.
     * Requires authentication (JWT).
     */
    @GetMapping("/balances")
    public ResponseEntity<Page<CustomerBalanceData>> getAllCustomersWithBalance(

            @RequestParam(defaultValue = "") String search,

            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable) {

        User user = securityUtils.getAuthenticatedUser();

        Page<CustomerBalanceData> customers =
                customerService
                        .getCustomersWithBalance(user.getId(), search, pageable)
                        .map(CustomerBalanceData::from);

        return ResponseEntity.ok(customers);
    }
}