package org.poo.system.cashback;

import org.poo.system.Commerciant;
import org.poo.system.ExchangeCurrency;
import org.poo.system.accounts.BankAccount;

import java.util.ArrayList;
import java.util.Map;

public final class SpendingThreshold implements CashBackStrategy {

    private static final double FIRST_RATE_THRESHOLD = 100.0;
    private static final double SECOND_RATE_THRESHOLD = 300.0;
    private static final double THIRD_RATE_THRESHOLD = 500.0;

    private static final Map<String, Double> FIRST_RATE = Map.of(
        "standard", 0.001,
        "student", 0.001,
        "silver", 0.003,
        "gold", 0.005
    );
    private static final Map<String, Double> SECOND_RATE = Map.of(
        "standard", 0.002,
        "student", 0.002,
        "silver", 0.004,
        "gold", 0.0055
    );
    private static final Map<String, Double> THIRD_RATE = Map.of(
        "standard", 0.0025,
        "student", 0.0025,
        "silver", 0.005,
        "gold", 0.007
    );

    @Override
    public double calculateCashBack(final BankAccount account, final Commerciant commerciant,
                                    final double amount) {
        ExchangeCurrency exchangeRates = ExchangeCurrency.getInstance();
        double amountInLei = exchangeRates.exchange(account.getCurrency(),
                "RON", amount, new ArrayList<>());
        if (amountInLei == -1) {
            return 0.0;
        }

        double total = account.getTotalSpent() + amountInLei;
        if (total >= THIRD_RATE_THRESHOLD) {
            return THIRD_RATE.get(account.getOwner().getPlan());

        } else if (total >= SECOND_RATE_THRESHOLD) {
            return SECOND_RATE.get(account.getOwner().getPlan());

        } else if (total >= FIRST_RATE_THRESHOLD) {
            return FIRST_RATE.get(account.getOwner().getPlan());
        }

        return 0.0;
    }
}
