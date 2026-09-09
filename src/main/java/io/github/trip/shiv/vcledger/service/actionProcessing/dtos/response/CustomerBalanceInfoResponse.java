package io.github.trip.shiv.vcledger.service.actionProcessing.dtos.response;

import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.enums.ResponseType;
import io.github.trip.shiv.vcledger.service.actionProcessing.dtos.preview.CustomerBalanceData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBalanceInfoResponse implements InfoResponse {

    private ResponseType responseType;
    private String message;

    private CustomerBalanceData customer;

    public CustomerBalanceInfoResponse(
            String message,
            CustomerBalanceData customer
    ) {
        this.responseType = ResponseType.INFORMATION;
        this.message = message;
        this.customer = customer;
    }
}