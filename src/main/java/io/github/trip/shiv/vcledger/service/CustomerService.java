package io.github.trip.shiv.vcledger.service;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.github.trip.shiv.vcledger.business.dtos.projections.CustomerBalanceProjection;
import io.github.trip.shiv.vcledger.business.exceptions.CustomerNotFoundException;
import io.github.trip.shiv.vcledger.business.imagestorage.CloudinaryUploadResult;
import io.github.trip.shiv.vcledger.business.imagestorage.ImageStorageService;
import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

/**
 * Business logic for Customer records.
 *
 * Responsibility split:
 *   CustomerController -> HTTP request/response only, delegates to this service
 *   CustomerService      -> business rules + ownership enforcement (this class)
 *   CustomerRepository    -> persistence access (unmodified, reused as-is)
 *   Customer              -> JPA entity (unmodified)
 *
 * Central security rule enforced throughout this class: every operation
 * that targets a specific customer is scoped to the authenticated user's
 * id, via CustomerRepository#findByIdAndUser_Id. A customer id belonging
 * to a different user is never distinguishable from a nonexistent one —
 * both surface as CustomerNotFoundException.
 *
 * Like UserService, this class takes a plain String email identifier
 * rather than an Authentication/HttpServletRequest object, so it stays
 * usable outside an HTTP context. Callers (controllers) are expected to
 * pass authentication.getName().
 */
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;
    
    private final ImageStorageService imageStorageService;
   
    /**
     * Create a customer owned by the authenticated user.
     *
     * Ownership is never taken from client input — the User is resolved
     * server-side from the authenticated email via the existing
     * UserService, and set directly on the new Customer. There is no
     * userId parameter here on purpose.
     *
     * @param currentUserEmail the authenticated user's email
     * @param name              the customer's name
     * @param phone             the customer's phone number (nullable, per entity)
     */
    @Transactional
    public Customer createCustomer(Long userId, String name, String phone, MultipartFile photoFile) {
        
    	User owner = userService.getUserById(userId);
    	
    	
        Customer customer = Customer.builder()
                .name(name)
                .phone(phone)
                .user(owner)
                .build();
        
        customer =  customerRepository.save(customer);

        if(photoFile != null) {
	        //Upload the image to cloudinary
	        CloudinaryUploadResult result =  imageStorageService.uploadImage(photoFile, "cust"+customer.getId());
	        
	        customer.setPhotoPublicId(result.getPublicId());
	        customer.setPhotoUrl(result.getSecureUrl());
        }
        return customer;
    }

    /**
     * All customers belonging to the authenticated user.
     * Never falls back to customerRepository.findAll().
     */
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers(Long userId) {
        return customerRepository.findByUser_Id(userId);  
    }
    
    
    @Transactional(readOnly = true)
    public Page<Customer> getCustomers(Long userId,Pageable pageable) {
        return customerRepository.findByUser_Id(userId,pageable);
        
    }

    /**
     * A single customer, verified to belong to the authenticated user.
     *
     * @throws CustomerNotFoundException if no such customer exists for this user
     *         (including the case where the id exists but belongs to someone else)
     */
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long userId, Long customerId) {
        return getCustomerOwnedByUser(userId, customerId);
    }
    

    /**
     * Update an existing customer's editable fields.
     *
     * Only `name` and `phone` are updatable — those are the only mutable,
     * non-relationship fields on the Customer entity. The `user` (owner)
     * relationship is never touched here, so a customer can never be
     * reassigned to a different user through this method. Blank/null
     * values are treated as "no change" so partial updates don't clobber
     * existing data.
     *
     * @throws CustomerNotFoundException if the customer doesn't exist or isn't owned by this user
     */
    @Transactional
    public Customer updateCustomer(Long userId, Long customerId, String newName, String newPhone) {
   
        Customer customer = getCustomerOwnedByUser(userId, customerId);

        if (StringUtils.hasText(newName)) {
            customer.setName(newName);
        }
        if (StringUtils.hasText(newPhone)) {
            customer.setPhone(newPhone);
        }

        return customerRepository.save(customer);
    }

    /**
     * Delete a customer, after verifying it belongs to the authenticated user.
     *
     * @throws CustomerNotFoundException if the customer doesn't exist or isn't owned by this user
     */
    @Transactional
    public void deleteCustomer(Long userId, Long customerId) {
        Customer customer = getCustomerOwnedByUser(userId, customerId);
        customerRepository.delete(customer);
    }

    /**
     * Fuzzy, case-insensitive name search restricted to the authenticated
     * user's own customers. Delegates directly to the existing repository
     * method — no new query logic needed.
     */
    @Transactional(readOnly = true)
    public List<Customer> searchCustomers(Long userId, String namePart) {
        return customerRepository.findByUser_IdAndNameContainingIgnoreCase(userId, namePart);
    }
    
    @Transactional(readOnly = true)
    public Page<Customer> searchCustomers(Long userId, String namePart,Pageable pageable) {
        return customerRepository.findByUser_IdAndNameContainingIgnoreCase(userId, namePart,pageable);
    }
    
    
    @Transactional(readOnly = true)
    public Optional<Customer> searchCustomerByPhone(Long userId,String customerPhone){
    	return customerRepository.findByUser_IdAndPhone(userId, customerPhone);
    }
    /**
     * Single choke point for ownership-checked customer lookup. Every
     * public method above that targets one specific customer routes
     * through here, so the "find + verify ownership" logic exists in
     * exactly one place instead of being repeated per method.
     */
    private Customer getCustomerOwnedByUser(Long userId, Long customerId) {
        return customerRepository.findByIdAndUser_Id(customerId, userId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + customerId));
    }
    
    
    
    /*
     * Get the CustomerBalanceProjection Response that also 
     * returns the Balance of Customer along with the Customer
     */
    @Transactional(readOnly = true)
    public Page<CustomerBalanceProjection> getCustomersWithBalance(Long userId,String search,Pageable pageable) {
    	return customerRepository
    			.findAllCustomersWithBalance(userId,search,pageable);
    }
}