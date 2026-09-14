package io.github.trip.shiv.vcledger.business.factories;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;

import io.github.trip.shiv.vcledger.business.dtos.ledger.intent.CreateTransactionOperation;
import io.github.trip.shiv.vcledger.business.dtos.ledger.intent.DeleteTransactionOperation;
import io.github.trip.shiv.vcledger.business.dtos.ledger.intent.LedgerOperation;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.ConfirmationResponse;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.DeletionSuccessResponse;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.LedgerResponse;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.SuccessResponse;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.TransactionConfirmationResponse;
import io.github.trip.shiv.vcledger.business.dtos.ledger.res.TransactionSuccessResponse;
import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.PersonInfo;
import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.TransactionData;
import io.github.trip.shiv.vcledger.business.dtos.visualpreviews.TransactionPreview;
import io.github.trip.shiv.vcledger.business.enums.OperationType;
import io.github.trip.shiv.vcledger.business.utilities.SecurityUtils;
import io.github.trip.shiv.vcledger.entity.Customer;
import io.github.trip.shiv.vcledger.entity.PendingOperation;
import io.github.trip.shiv.vcledger.entity.User;
import io.github.trip.shiv.vcledger.service.CustomerService;
import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor
public class LedgerResponseFactory {
	
	
	LedgerOperationFactory ledgerOperationFactory;
	CustomerService customerService;
	SecurityUtils securityUtils;
	
	public LedgerResponse getConfirmationResponse(PendingOperation pendingOperation) {
		
		LedgerOperation operation = ledgerOperationFactory.fromPendingOperation(pendingOperation);
		
		String operationId = pendingOperation.getOperationId();
		Instant expiry = pendingOperation.getExpiresAt();
		return getConfirmationResponse(operation,operationId,expiry);
	
	}
	
	
	
	private ConfirmationResponse getConfirmationResponse(LedgerOperation operation,String operationId,Instant expiresAt) {
		
		User user = securityUtils.getAuthenticatedUser();
		
		
		if(operation instanceof CreateTransactionOperation createTransactionOperation) {
			
			//Load the Customer
			Customer customer = 
					customerService.getCustomerById(user.getEmail(), createTransactionOperation.getCustomerId());
			
				BigDecimal amount = createTransactionOperation.getAmount();
				
				return new TransactionConfirmationResponse(
						"Confirm the Operation to be done",
						operationId,
						operation.getType(),
						new TransactionPreview(
								PersonInfo.fromShopkeeper(securityUtils.getAuthenticatedUser()),
								PersonInfo.fromCustomer(customer),
								amount,
								createTransactionOperation.getMoneyDirection()));
			
			
		}
		
		throw new IllegalArgumentException("PendingOperation can't be converted into LedgerResponse...");
	}
	
	
	
	
	/*
	 * TO get the TransactionSuccessResponse when certain transaction succeeds
	 * TO get the DeletionSuccessResponse when certain transaction deletes
	 */
	public SuccessResponse getSuccessResponse(PendingOperation pendingOperation) {
		
		
		
		
		User user = securityUtils.getAuthenticatedUser();
		
		LedgerOperation operation = ledgerOperationFactory.fromPendingOperation(pendingOperation);
		
		if(operation instanceof DeleteTransactionOperation operation1) {
			
			return new DeletionSuccessResponse(
					"The Transaction has been deleted successfully",
					OperationType.DELETE_TRANSACTION,
					null);
						
		}else if (operation instanceof CreateTransactionOperation operation1) {
			
			//Load the Customer
			Customer customer = 
					customerService.getCustomerById(user.getEmail(), operation1.getCustomerId());
			
				BigDecimal amount = operation1.getAmount();
			
			return new TransactionSuccessResponse(
					"The Transaction has been created successfully",
					new TransactionData(
							PersonInfo.fromShopkeeper(user),
							PersonInfo.fromCustomer(customer),
							amount,
							operation1.getMoneyDirection(), 
							pendingOperation.getExecutedAt()));
		}
		
		throw new IllegalArgumentException("PendingOperation could not be converted into a SuccessResponse");
	}

}
