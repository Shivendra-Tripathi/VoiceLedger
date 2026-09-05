package io.github.trip.shiv.vcledger.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.repository.CustomerRepository;
import io.github.trip.shiv.vcledger.service.exception.CustomerNotFoundException;

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
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;

    public CustomerService(CustomerRepository customerRepository, UserService userService) {
        this.customerRepository = customerRepository;
        this.userService = userService;
    }

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
    public Customer createCustomer(String currentUserEmail, String name, String phone) {
        User owner = userService.getUserByEmail(currentUserEmail);

        Customer customer = Customer.builder()
                .name(name)
                .phone(phone)
                .user(owner)
                .build();

        return customerRepository.save(customer);
    }

    /**
     * All customers belonging to the authenticated user.
     * Never falls back to customerRepository.findAll().
     */
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers(String currentUserEmail) {
        User owner = userService.getUserByEmail(currentUserEmail);
        return customerRepository.findByUser_Id(owner.getId());
    }

    /**
     * A single customer, verified to belong to the authenticated user.
     *
     * @throws CustomerNotFoundException if no such customer exists for this user
     *         (including the case where the id exists but belongs to someone else)
     */
    @Transactional(readOnly = true)
    public Customer getCustomerById(String currentUserEmail, Long customerId) {
        User owner = userService.getUserByEmail(currentUserEmail);
        return getCustomerOwnedByUser(owner.getId(), customerId);
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
    public Customer updateCustomer(String currentUserEmail, Long customerId, String newName, String newPhone) {
        User owner = userService.getUserByEmail(currentUserEmail);
        Customer customer = getCustomerOwnedByUser(owner.getId(), customerId);

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
    public void deleteCustomer(String currentUserEmail, Long customerId) {
        User owner = userService.getUserByEmail(currentUserEmail);
        Customer customer = getCustomerOwnedByUser(owner.getId(), customerId);
        customerRepository.delete(customer);
    }

    /**
     * Fuzzy, case-insensitive name search restricted to the authenticated
     * user's own customers. Delegates directly to the existing repository
     * method — no new query logic needed.
     */
    @Transactional(readOnly = true)
    public List<Customer> searchCustomers(String currentUserEmail, String namePart) {
        User owner = userService.getUserByEmail(currentUserEmail);
        return customerRepository.findByUser_IdAndNameContainingIgnoreCase(owner.getId(), namePart);
    }
    
    
    @Transactional(readOnly = true)
    public Optional<Customer> searchCustomerByPhone(String currentUserEmail,String customerPhone){
    	User owner = userService.getUserByEmail(currentUserEmail);
    	return customerRepository.findByUser_IdAndPhone(owner.getId(), customerPhone);
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
}