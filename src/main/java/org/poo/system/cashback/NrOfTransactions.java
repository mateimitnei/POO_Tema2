package org.poo.system.cashback;

import org.poo.system.Commerciant;
import org.poo.system.accounts.BankAccount;

import java.util.Map;

public final class NrOfTransactions implements CashBackStrategy {

    private static final Map<String, Double> rates = Map.of(
        "Food", 0.02,
        "Clothes", 0.05,
        "Tech", 0.1
    );

    @Override
    public double calculateCashBack(final BankAccount account, final Commerciant commerciant,
                                    final double amount) {
        if (account.getDiscounts().getOrDefault(commerciant.getType(), false)) {
            account.getDiscounts().put(commerciant.getType(), false);
            return rates.get(commerciant.getType());
        }
        return 0.0;
    }
}
