package org.poo.system.transactions;

import org.poo.fileio.CommandInput;

import java.util.Map;

public final class TransactionFactory {

    private TransactionFactory() { }

    /**
     * Creates a new transaction based on the command input.
     *
     * @param input the command input
     * @param params the mapped extra parameters for the transaction
     * @return the new transaction
     */
    public static Transaction createTransaction(final CommandInput input,
                                                final Map<String, String> params) {
        return switch (input.getCommand()) {
            case "sendMoney" -> new BankTransfer(input.getTimestamp(), input.getDescription(),
                    input.getAccount(), input.getReceiver(),
                    Double.parseDouble(params.get("amount")), params.get("currency"),
                    params.get("type"));
            case "createCard", "createOneTimeCard" -> new CardCreated(input.getTimestamp(),
                    params.get("cardNumber"), input.getEmail(), input.getAccount());
            case "deleteCard" -> new CardDeleted(input.getTimestamp(), input.getCardNumber(),
                    input.getEmail(), params.get("account"));
            case "payOnline" -> new CardPayment(input.getTimestamp(),
                    Double.parseDouble(params.get("amount")), params.get("commerciant"),
                    params.get("iban"));
            case "splitPayment" -> new PayedWithOthers(input, params.get("errorIBAN"),
                    params.get("rejected"));
            case "changeInterestRate" -> new RateChanged(input.getTimestamp(),
                    input.getInterestRate());
            case "addInterest" -> new InterestAdded(input.getTimestamp(),
                    Double.parseDouble(params.get("amount")), params.get("currency"));
            case "upgradePlan" -> new PlanUpgraded(input.getTimestamp(), input.getAccount(),
                    params.get("plan"));
            case "cashWithdrawal" -> new WithdrewCash(input.getTimestamp(),
                    Double.parseDouble(params.get("amount")));
            case "withdrawSavings" -> new WithdrewSavings(input.getTimestamp(), input.getAccount(),
                    Double.parseDouble(params.get("amount")), params.get("classicIBAN"));
            default -> throw new IllegalArgumentException("Invalid transaction type.");
        };
    }
}
