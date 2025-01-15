package org.poo.system.cashback;

import org.poo.system.Commerciant;
import org.poo.system.accounts.BankAccount;

public interface CashBackStrategy {

    double calculateCashBack(BankAccount account, Commerciant commerciant, double amount);
}
