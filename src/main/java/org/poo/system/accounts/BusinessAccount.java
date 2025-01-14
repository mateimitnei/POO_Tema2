package org.poo.system.accounts;

import org.poo.system.User;

public final class BusinessAccount extends BankAccount {
    public BusinessAccount(final String currency, final User owner) {
        super(currency, owner);
    }

    public String getAccountType() {
        return "business";
    }
}
