package org.poo.system.cashback;

public class CashBackFactory {

    private CashBackFactory() { }

    /**
     * Creates a new cashback strategy based on the input.
     *
     * @param type the cashback strategy type
     * @return the created cashback strategy
     */
    public static CashBackStrategy createCashBackStrategy(final String type) {
        return switch (type) {
            case "nrOfTransactions" -> new NrOfTransactions();
            case "spendingThreshold" -> new SpendingThreshold();
            default -> throw new IllegalArgumentException("Invalid cashback strategy.");
        };
    }
}
