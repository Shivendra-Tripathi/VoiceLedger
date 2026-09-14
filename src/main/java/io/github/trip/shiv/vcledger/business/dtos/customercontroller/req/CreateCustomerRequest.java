package io.github.trip.shiv.vcledger.business.dtos.customercontroller.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequest {
	private String name;
	private String phone;
}
