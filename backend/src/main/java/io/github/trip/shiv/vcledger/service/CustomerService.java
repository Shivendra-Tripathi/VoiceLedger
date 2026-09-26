
package io.github.trip.shiv.vcledger.service;

import io.github.trip.shiv.vcledger.core.dtos.projections.CustomerBalanceProjection;
import io.github.trip.shiv.vcledger.core.dtos.projections.CustomerNameAndIdProjection;
import io.github.trip.shiv.vcledger.core.exceptions.custom.business.PhoneNumberAlreadyExistsException;
import io.github.trip.shiv.vcledger.core.exceptions.custom.internal.CustomerNotFoundException;
import io.github.trip.shiv.vcledger.core.external.imagestorage.CloudinaryUploadResult;
import io.github.trip.shiv.vcledger.core.external.imagestorage.ImageStorageService;
import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserService userService;
    private final ImageStorageService imageStorageService;

    @Transactional
    public Customer createCustomer(
            Long userId,
            String name,
            String phone,
            MultipartFile photoFile) {

        User owner = userService.getUserById(userId);

        if (phone == null || searchCustomerByPhone(userId, phone.trim()).isPresent()) {
            throw new PhoneNumberAlreadyExistsException(
                    "The Customer can't be registered as Phone number is Duplicate");
        }

        Customer customer = Customer.builder()
                .name(name.trim())
                .phone(phone.trim())
                .user(owner)
                .build();

        customer = customerRepository.save(customer);

        if (photoFile != null) {
            CloudinaryUploadResult result =
                    imageStorageService.uploadImage(photoFile, "cust" + customer.getId());

            customer.setPhotoPublicId(result.getPublicId());
            customer.setPhotoUrl(result.getSecureUrl());
        }

        return customer;
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers(Long userId) {
        return customerRepository.findByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public Page<Customer> getCustomers(Long userId, Pageable pageable) {
        return customerRepository.findByUser_Id(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Long userId, Long customerId) {
        return getCustomerOwnedByUser(userId, customerId);
    }

    @Transactional
    public Customer updateCustomer(
            Long userId,
            Long customerId,
            String newName,
            String newPhone,
            MultipartFile photoFile) {

        Customer customer = getCustomerOwnedByUser(userId, customerId);

        if (StringUtils.hasText(newName)) {
            customer.setName(newName.trim());
        }

        if (StringUtils.hasText(newPhone)) {
            String normalizedPhone = newPhone.trim();

            if (!normalizedPhone.equals(customer.getPhone())
                    && searchCustomerByPhone(userId, normalizedPhone).isPresent()) {
                throw new PhoneNumberAlreadyExistsException(
                        "The Customer can't be updated as Phone number is Duplicate");
            }

            customer.setPhone(normalizedPhone);
        }

        if (photoFile != null) {
            if (StringUtils.hasText(customer.getPhotoPublicId())) {
                imageStorageService.deleteImage(customer.getPhotoPublicId());
            }

            CloudinaryUploadResult result =
                    imageStorageService.uploadImage(photoFile, "cust" + customer.getId());

            customer.setPhotoPublicId(result.getPublicId());
            customer.setPhotoUrl(result.getSecureUrl());
        }

        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Long userId, Long customerId) {
        Customer customer = getCustomerOwnedByUser(userId, customerId);

        if (StringUtils.hasText(customer.getPhotoPublicId())) {
            imageStorageService.deleteImage(customer.getPhotoPublicId());
        }

        customerRepository.delete(customer);
    }

    @Transactional(readOnly = true)
    public List<Customer> searchCustomers(Long userId, String namePart) {
        return customerRepository.findByUser_IdAndNameContainingIgnoreCase(
                userId, namePart);
    }

    @Transactional(readOnly = true)
    public Page<Customer> searchCustomers(
            Long userId,
            String namePart,
            Pageable pageable) {

        return customerRepository.findByUser_IdAndNameContainingIgnoreCase(
                userId, namePart, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Customer> searchCustomerByPhone(
            Long userId,
            String customerPhone) {

        return customerRepository.findByUser_IdAndPhone(
                userId, customerPhone.trim());
    }

    private Customer getCustomerOwnedByUser(Long userId, Long customerId) {
        return customerRepository.findByIdAndUser_Id(customerId, userId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with id: " + customerId));
    }

    @Transactional(readOnly = true)
    public Page<CustomerBalanceProjection> getCustomersWithBalance(
            Long userId,
            String search,
            Pageable pageable) {

        return customerRepository.findAllCustomersWithBalance(
                userId, search, pageable);
    }

    @Transactional(readOnly = true)
    public List<CustomerNameAndIdProjection> getCustomersNameAndIdProjections(
            Long userId) {

        return customerRepository.findAllCustomerNameAndIdBelongingToUser(userId);
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomersInProjectionList(
            Long userId,
            List<CustomerNameAndIdProjection> customers) {

        return customerRepository.findAllCustomersWhichAreIn(
                userId,
                customers.stream()
                        .map(CustomerNameAndIdProjection::getId)
                        .toList());
    }
}
