package io.github.trip.shiv.vcledger.business.dtos.visualpreviews;
import io.github.trip.shiv.vcledger.business.enums.PersonType;
import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfo {

    private Long id;

    private String name;

    private String photoUrl;

    private PersonType type;
    
    public static PersonInfo fromCustomer(Customer customer) {
    	PersonInfo personInfo =
    			new PersonInfo(
    					customer.getId(),
    					customer.getName(),
    					null,
    					PersonType.CUSTOMER);
    	return personInfo;
    }
    
    public static PersonInfo fromShopkeeper(User user) {
    	PersonInfo personInfo = 
    			new PersonInfo(
    					user.getId(),
    					user.getName(),
    					null,
    					PersonType.SHOPKEEPER);
    	return personInfo;
    }
}