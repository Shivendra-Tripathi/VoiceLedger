package io.github.trip.shiv.vcledger.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.github.trip.shiv.vcledger.core.dtos.projections.CustomerNameAndIdProjection;
import io.github.trip.shiv.vcledger.core.utilities.PhoneticNameMatcher;
import io.github.trip.shiv.vcledger.entity.Customer;

@Service
public class CustomerNameMatcherService {

	private final PhoneticNameMatcher<CustomerNameAndIdProjection> phoneticNameMatcher;
	private final CustomerService customerService;
	
	private final Double MIN_CONFIDENCE = 0.65d;
	
	public CustomerNameMatcherService(CustomerService customerService) {
		this.customerService = customerService;
		phoneticNameMatcher = 
				new PhoneticNameMatcher<CustomerNameAndIdProjection>(
				CustomerNameAndIdProjection::getName);
	}
	
	public List<Customer> getMatchingCustomers(Long userId,String spokenName) {
		
		//Load all the Customers belonging to the User
		List<CustomerNameAndIdProjection> allCustomerNames = 
				customerService.getCustomersNameAndIdProjections(userId);
		
		//Get All Matching/Similarity Match Customers
		List<CustomerNameAndIdProjection> returnedNames =
				phoneticNameMatcher.findSimilarMatches(spokenName, allCustomerNames, MIN_CONFIDENCE);
		
		//Convert the found Customer Names into the Actual Customers
		List<Customer> customers =
				customerService.getAllCustomersInProjectionList(userId,returnedNames);
		
		return customers;
		
	}
}
