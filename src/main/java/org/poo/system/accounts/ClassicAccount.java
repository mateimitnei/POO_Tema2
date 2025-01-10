package org.poo.system.accounts;

public final class ClassicAccount extends BankAccount {

    public ClassicAccount(final String currency) {
        super(currency);
    }

    public String getAccountType() {
        return "classic";
    }
}
