package org.poo.system.commands;

import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.ExchangeCurrency;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.Transaction;
import org.poo.system.transactions.TransactionFactory;

import java.util.ArrayList;
import java.util.Map;

public class UpgradePlan implements Strategy {

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

                    if ((account.getOwner().getPlan().equals("standard")
                            || account.getOwner().getPlan().equals("student"))
                            && input.getNewPlanType().equals("silver")) {

                        converted = exchangeRates.exchange("RON",
                                account.getCurrency(), STANDARD_TO_SILVER, new ArrayList<>());

                        System.out.println(STANDARD_TO_SILVER);

                    } else if (account.getOwner().getPlan().equals("silver")
                            && input.getNewPlanType().equals("gold")) {

                        converted = exchangeRates.exchange("RON",
                                account.getCurrency(), SILVER_TO_GOLD, new ArrayList<>());

                        System.out.println(SILVER_TO_GOLD);

                    } else if ((account.getOwner().getPlan().equals("standard")
                            || account.getOwner().getPlan().equals("student"))
                            && input.getNewPlanType().equals("gold")) {

                        converted = exchangeRates.exchange("RON",
                                account.getCurrency(), STANDARD_TO_GOLD, new ArrayList<>());

                        System.out.println(STANDARD_TO_GOLD);
                    }

                    if (converted > 0.0) {

                        try {
                            //
                            System.out.println("Balance: " + account.getBalance());

                            account.withdraw(converted);

                            account.getOwner().setPlan(input.getNewPlanType());
                            account.addToTransactionLog(TransactionFactory.createTransaction(input,
                                    Map.of("plan", input.getNewPlanType())));

                            //
                            System.out.println("Plan upgraded: " + converted + account.getCurrency() + ", User: " + user.getEmail());
                            System.out.println("Balance: " + account.getBalance());

                        } catch (ArithmeticException e) {
                            account.addToTransactionLog(new Transaction(input.getTimestamp(),
                                    "Insufficient funds"));
                            return;
                        }

                        return;
                    }


                    return;
                }
            }
        }
    }
}
