package org.poo.system.cashback;

import org.poo.system.Commerciant;
import org.poo.system.accounts.BankAccount;

public interface CashBackStrategy {

    /**
     * Calculate the cash back rate for a given account and amount
     * @param account the account to calculate the cash back
     * @param amount the amount to verify the spendingThreshold cash back
     * @return the cash back rate
     */
    double calculateCashBack(BankAccount account, Commerciant commerciant, double amount);
}
