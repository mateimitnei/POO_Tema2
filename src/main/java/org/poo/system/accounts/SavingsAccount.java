package org.poo.system.accounts;

import lombok.Getter;
import lombok.Setter;

public final class SavingsAccount extends BankAccount {
    @Getter @Setter
    private double interestRate;

    public SavingsAccount(final String currency, final double interestRate) {
        super(currency);
        this.interestRate = interestRate;
    }

    public String getAccountType() {
        return "savings";
    }
}
