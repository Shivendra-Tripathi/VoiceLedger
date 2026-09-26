package io.github.trip.shiv.vcledger.controller;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.trip.shiv.vcledger.core.dtos.customercontroller.req.CreateCustomerRequest;
import io.github.trip.shiv.vcledger.core.dtos.customercontroller.req.UpdateCustomerRequest;
import io.github.trip.shiv.vcledger.core.dtos.general.res.MessageResponse;
import io.github.trip.shiv.vcledger.core.dtos.transactioncontroller.res.TransactionResponse;
import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.CustomerBalanceData;
import io.github.trip.shiv.vcledger.core.dtos.visualpreviews.PersonInfo;
import io.github.trip.shiv.vcledger.core.utilities.SecurityUtils;
import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.service.CustomerService;
import io.github.trip.shiv.vcledger.service.TransactionService;
import jakarta.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	private final SecurityUtils securityUtils;
	private final CustomerService customerService;
	private final TransactionService transactionService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CustomerBalanceData> createCustomer(
			@Valid @RequestPart("customer") CreateCustomerRequest request,
			@RequestPart(value = "image", required = false) MultipartFile image) {

		User user = securityUtils.getAuthenticatedUser();

		Customer customer = customerService.createCustomer(
				user.getId(),
				request.getName(),
				request.getPhone(),
				image
		);

		CustomerBalanceData response = CustomerBalanceData.builder()
				.balance(BigDecimal.ZERO)
				.customer(PersonInfo.fromCustomer(customer))
				.build();

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(response);
	}

	@GetMapping
	public ResponseEntity<Page<PersonInfo>> getAllCustomers(
			@PageableDefault(
					page = 0,
					size = 10,
					sort = "createdAt",
					direction = Sort.Direction.ASC
			)
			Pageable pageable) {

		User user = securityUtils.getAuthenticatedUser();

		Page<PersonInfo> personInfos = customerService
				.getCustomers(user.getId(), pageable)
				.map(PersonInfo::fromCustomer);

		return ResponseEntity.ok(personInfos);
	}

	@GetMapping("/{id}")
	public ResponseEntity<PersonInfo> getCustomerById(@PathVariable Long id) {

		User user = securityUtils.getAuthenticatedUser();
		Customer customer = customerService.getCustomerById(user.getId(), id);

		return ResponseEntity.ok(PersonInfo.fromCustomer(customer));
	}

	@PutMapping(
			value = "/{id}",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<PersonInfo> updateCustomer(
			@PathVariable Long id,
			@Valid @RequestPart("customer") UpdateCustomerRequest request,
			@RequestPart(value = "image", required = false) MultipartFile image) {

		User user = securityUtils.getAuthenticatedUser();

		Customer customer = customerService.updateCustomer(
				user.getId(),
				id,
				request.getNewName(),
				request.getNewPhone(),
				image
		);

		return ResponseEntity.ok(PersonInfo.fromCustomer(customer));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<MessageResponse> deleteCustomer(@PathVariable Long id) {

		User user = securityUtils.getAuthenticatedUser();

		customerService.deleteCustomer(user.getId(), id);

		return ResponseEntity.ok(
				new MessageResponse("Successfully Deleted Customer.")
		);
	}

	@GetMapping("/search/{name}")
	public ResponseEntity<Page<PersonInfo>> searchCustomers(
			@PathVariable String name,
			@PageableDefault(
					page = 0,
					size = 10,
					sort = "createdAt",
					direction = Sort.Direction.DESC
			)
			Pageable pageable) {

		User user = securityUtils.getAuthenticatedUser();

		Page<PersonInfo> customerResponses = customerService
				.searchCustomers(user.getId(), name, pageable)
				.map(PersonInfo::fromCustomer);

		return ResponseEntity.ok(customerResponses);
	}


	@GetMapping("/{customerId}/balance")
	public ResponseEntity<CustomerBalanceData> getCustomerBalance(
			@PathVariable Long customerId) {

		User user = securityUtils.getAuthenticatedUser();

		Customer customer = customerService.getCustomerById(user.getId(), customerId);
		BigDecimal balance = transactionService.getCustomerBalance(
				user.getId(),
				customerId
		);

		CustomerBalanceData response = CustomerBalanceData.builder()
				.balance(balance)
				.customer(PersonInfo.fromCustomer(customer))
				.build();

		return ResponseEntity.ok(response);
	}

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

		Page<CustomerBalanceData> customers = customerService
				.getCustomersWithBalance(user.getId(), search, pageable)
				.map(CustomerBalanceData::from);

		return ResponseEntity.ok(customers);
	}
}
