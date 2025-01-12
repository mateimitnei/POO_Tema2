package org.poo.system.accounts;

public class BusinessAccount extends BankAccount {
    public BusinessAccount(final String currency) {
        super(currency);
    }

    public String getAccountType() {
        return "business";
    }
}
