package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.*;
import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.Transaction;
import org.poo.system.transactions.TransactionFactory;

import java.util.ArrayList;
import java.util.Map;

public final class UpgradePlan implements Strategy {

    private static final double STANDARD_TO_SILVER = 100.0;
    private static final double SILVER_TO_GOLD = 250.0;
    private static final double STANDARD_TO_GOLD = 350.0;

    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();
        ExchangeCurrency exchangeRates = ExchangeCurrency.getInstance();
        double converted = 0.0;

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {

                    // If the user already has that plan
                    if (user.getPlan().equals(input.getNewPlanType())) {
                        account.addToTransactionLog(new Transaction(input.getTimestamp(),
                                "The user already has the " + user.getPlan() + " plan."));
                        return;
                    }

                    // If the user tries to downgrade from gold to silver
                    if (user.getPlan().equals("gold") && input.getNewPlanType().equals("silver")) {
                        account.addToTransactionLog(new Transaction(input.getTimestamp(),
                                "You cannot downgrade your plan."));
                        return;
                    }

                    if ((account.getOwner().getPlan().equals("standard")
                            || account.getOwner().getPlan().equals("student"))
                            && input.getNewPlanType().equals("silver")) {

                        converted = exchangeRates.exchange("RON",
                                account.getCurrency(), STANDARD_TO_SILVER, new ArrayList<>());

                        System.out.println("    STANDARD_TO_SILVER "
                                + STANDARD_TO_SILVER + ", " + user.getEmail());

                    } else if (account.getOwner().getPlan().equals("silver")
                            && input.getNewPlanType().equals("gold")) {

                        converted = exchangeRates.exchange("RON",
                                account.getCurrency(), SILVER_TO_GOLD, new ArrayList<>());

                        System.out.println("    SILVER_TO_GOLD "
                                + SILVER_TO_GOLD + ", " + user.getEmail());

                    } else if ((account.getOwner().getPlan().equals("standard")
                            || account.getOwner().getPlan().equals("student"))
                            && input.getNewPlanType().equals("gold")) {

                        converted = exchangeRates.exchange("RON",
                                account.getCurrency(), STANDARD_TO_GOLD, new ArrayList<>());

                        System.out.println("    STANDARD_TO_GOLD "
                                + STANDARD_TO_GOLD + ", " + user.getEmail());
                    }

                    System.out.println("    Balance: "
                            + account.getBalance() + " " + account.getCurrency());

                    if (converted > 0.0) {
                        String oldPlan = user.getPlan();
                        try {
                            user.setPlan(input.getNewPlanType());
                            account.withdraw(converted, false);
                            // user.setSpendingThreshold(0.0);

                            account.addToTransactionLog(TransactionFactory.createTransaction(input,
                                    Map.of("plan", input.getNewPlanType())));

                            System.out.println("    Plan upgraded -> "
                                    + converted + " " + account.getCurrency());
                            System.out.println("    Balance: "
                                    + account.getBalance() + " " + account.getCurrency());
                            System.out.println("    Timestamp: "
                                    + input.getTimestamp());

                        } catch (ArithmeticException e) {
                            account.addToTransactionLog(new Transaction(input.getTimestamp(),
                                    "Insufficient funds"));
                            user.setPlan(oldPlan);

                            System.out.println("    Timestamp: "
                                    + input.getTimestamp() + ": Insufficient funds");
                        }

                        System.out.println("\n");
                        return;
                    }

                    return;
                }
            }
        }

        // If the account was not found
        ObjectNode commandOutput = TheNotFoundError
                .makeOutput(input, engine.getObjectMapper(), "Account not found");

        Output.getInstance().getOutput().add(commandOutput);
    }
}
