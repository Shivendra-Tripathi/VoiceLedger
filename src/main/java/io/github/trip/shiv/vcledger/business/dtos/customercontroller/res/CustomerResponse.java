package io.github.trip.shiv.vcledger.business.dtos.customercontroller.res;

import java.time.LocalDateTime;

import io.github.trip.shiv.vcledger.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
	
	Long id;
	String name;
	String phone;
	LocalDateTime createdAt;
	
	public CustomerResponse(Customer customer) {
		id = customer.getId();
		name = customer.getName();
		phone = customer.getPhone();
		createdAt = customer.getCreatedAt();
	}
}
