package org.poo.system.cashback;

import org.poo.system.accounts.BankAccount;

public interface CashBackStrategy {
    double calculateCashBack(final BankAccount account);
}
