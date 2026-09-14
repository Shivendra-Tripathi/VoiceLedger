package io.github.trip.shiv.vcledger.business.enums;

public enum MoneyDirection {
    CUSTOMER_TO_SHOPKEEPER,
    SHOPKEEPER_TO_CUSTOMER;
	
	public static  MoneyDirection  from(TransactionType transactionType) {
		if(transactionType==TransactionType.CREDIT) {
			return CUSTOMER_TO_SHOPKEEPER;
		}
		return SHOPKEEPER_TO_CUSTOMER;
	}
}