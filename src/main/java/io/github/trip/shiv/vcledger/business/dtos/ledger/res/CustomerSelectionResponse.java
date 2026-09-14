package io.github.trip.shiv.vcledger.business.dtos.ledger.res;

import java.util.ArrayList;
import java.util.List;

import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.PersonInfo;
import io.github.trip.shiv.vcledger.business.enums.ResponseType;
import io.github.trip.shiv.vcledger.entity.Customer;
import lombok.Getter;


@Getter
public class CustomerSelectionResponse implements SelectionResponse{
	
	
	private final String message;
	private final List<PersonInfo> customers;
	
	public CustomerSelectionResponse(String message,List<Customer> customers) {
		
		this.message = message;
		this.customers = new ArrayList<PersonInfo>();
		
		for(Customer customer : customers) {
			PersonInfo personInfo =
					PersonInfo.fromCustomer(customer);
			this.customers.add(personInfo);
		}
	}
	
	
	@Override
	public ResponseType getResponseType() {
		return ResponseType.SELECTION;
	}

	@Override
	public String getMessage() {
		return message;
	}

}
