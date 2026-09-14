package io.github.trip.shiv.vcledger.business.enums;

public enum TransactionType {
    CREDIT,
    DEBIT;
	
	public static TransactionType from(MoneyDirection moneyDirection) {
		if(moneyDirection==MoneyDirection.CUSTOMER_TO_SHOPKEEPER)
			return CREDIT;
		return DEBIT;
	}
}