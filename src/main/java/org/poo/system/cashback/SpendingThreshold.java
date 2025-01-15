package org.poo.system.cashback;

import org.poo.system.Commerciant;
import org.poo.system.accounts.BankAccount;

import java.util.Map;

public final class SpendingThreshold implements CashBackStrategy {

    private static final Map<String, Double> firstRate = Map.of(
        "standard", 0.001,
        "student", 0.001,
        "silver", 0.003,
        "gold", 0.005
    );
    private static final Map<String, Double> secondRate = Map.of(
        "standard", 0.002,
        "student", 0.002,
        "silver", 0.004,
        "gold", 0.0055
    );
    private static final Map<String, Double> thirdRate = Map.of(
        "standard", 0.0025,
        "student", 0.0025,
        "silver", 0.005,
        "gold", 0.007
    );

    @Override
    public double calculateCashBack(final BankAccount account, final Commerciant commerciant,
                                    final double amount) {
        double total = account.getTotalSpent() + amount;
        if (total >= 500.0) {
            return thirdRate.get(account.getOwner().getPlan());

        } else if (total >= 300.0) {
            return secondRate.get(account.getOwner().getPlan());

        } else if (total >= 100.0) {
            return firstRate.get(account.getOwner().getPlan());
        }

        return 0.0;
    }
}
