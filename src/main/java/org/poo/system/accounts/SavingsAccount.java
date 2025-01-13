package org.poo.system.accounts;

import lombok.Getter;
import lombok.Setter;
import org.poo.system.User;

public final class SavingsAccount extends BankAccount {
    @Getter @Setter
    private double interestRate;

    public SavingsAccount(final String currency, final double interestRate, final User owner) {
        super(currency, owner);
        this.interestRate = interestRate;
    }

    public String getAccountType() {
        return "savings";
    }
}
