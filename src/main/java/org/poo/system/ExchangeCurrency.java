package org.poo.system;

import lombok.Getter;
import org.poo.fileio.ExchangeInput;

import java.util.ArrayList;
import java.util.List;

public final class ExchangeCurrency {
    @Getter
    private List<ExchangeRate> exchangeRates;
    private static ExchangeCurrency instance;

    private ExchangeCurrency() {
        this.exchangeRates = new ArrayList<>();
    }

    /**
     * Singleton pattern.
     * @return the instance of the class
     */
    public static ExchangeCurrency getInstance() {
        if (instance == null) {
            instance = new ExchangeCurrency();
        }
        return instance;
    }

    /**
     * Initialize the exchange rates.
     * @param exchangeRates the exchange rates
     */
    public void init(final ExchangeInput[] exchangeRates) {
        this.exchangeRates.clear();

        for (ExchangeInput rate : exchangeRates) {
            ExchangeRate rateCpy = new ExchangeRate(rate, false);
            this.exchangeRates.add(rateCpy);

            ExchangeRate reversed = new ExchangeRate(rate, true);
            this.exchangeRates.add(reversed);
        }
    }

    /**
     * Exchange an amount of money from a currency to another.
     * @param from the currency to exchange from
     * @param to the currency to exchange to
     * @param amount the amount to exchange
     * @param visited the list of visited currencies
     * @return the exchanged amount
     */
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
