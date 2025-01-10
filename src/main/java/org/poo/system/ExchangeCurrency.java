package org.poo.system;

import org.poo.fileio.ExchangeInput;

import java.util.ArrayList;
import java.util.List;

public final class ExchangeCurrency {
    private final List<ExchangeRate> exchangeRates;

    public ExchangeCurrency(final ExchangeInput[] exchangeRates) {
        this.exchangeRates = new ArrayList<>();

        for (ExchangeInput rate : exchangeRates) {
            ExchangeRate rateCpy = new ExchangeRate(rate, false);
            this.exchangeRates.add(rateCpy);

            ExchangeRate reversed = new ExchangeRate(rate, true);
            this.exchangeRates.add(reversed);
        }
    }

    public double exchange(final String from, final String to, final double amount,
                                    final List<String> visited) {
        if (from.equals(to)) {
            return amount;
        }

        visited.add(from);

        for (ExchangeRate rate : exchangeRates) {
            if (rate.getFrom().equals(from) && !visited.contains(rate.getTo())) {
                double convertedAmount = amount * rate.getRate();
                double result = exchange(rate.getTo(), to, convertedAmount, visited);
                if (result != -1) {
                    return result;
                }
            }
        }

        return -1;
    }
}
