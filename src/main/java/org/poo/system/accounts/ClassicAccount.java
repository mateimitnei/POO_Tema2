package org.poo.system.accounts;

import org.poo.system.User;

public final class ClassicAccount extends BankAccount {

    public ClassicAccount(final String currency, final User owner) {
        super(currency, owner);
    }

    public String getAccountType() {
        return "classic";
    }
}
