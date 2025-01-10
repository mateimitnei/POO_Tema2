package org.poo.system;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.ExchangeInput;

@Setter @Getter
public class ExchangeRate {
    private String from;
    private String to;
    private double rate;

    /**
     * Constructor for ExchangeRate.
     * @param rate the exchange rate input
     * @param reverse if the rate should be reversed
     */
    public ExchangeRate(final ExchangeInput rate, final boolean reverse) {
        if (!reverse) {
            this.from = rate.getFrom();
            this.to = rate.getTo();
            this.rate = rate.getRate();
        } else {
            this.from = rate.getTo();
            this.to = rate.getFrom();
            this.rate = 1 / rate.getRate();
        }
    }
}
