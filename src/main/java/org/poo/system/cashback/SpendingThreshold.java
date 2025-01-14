package org.poo.system.cashback;

import org.poo.system.accounts.BankAccount;

public final class SpendingThreshold implements CashBackStrategy {
    @Override
    public double calculateCashBack(final BankAccount account) {
        return 0.0;
    }
}
