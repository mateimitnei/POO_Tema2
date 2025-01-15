package org.poo.system.commands;

import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.ExchangeCurrency;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.TransactionFactory;

import java.util.ArrayList;
import java.util.Map;

public final class SplitPayment implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();
        ExchangeCurrency exchangeRates = ExchangeCurrency.getInstance();

        double splitAmount = input.getAmount() / input.getAccounts().size();

        String incompatibleAccount = "";
        for (String accountIban : input.getAccounts()) {
            for (User user : engine.getUsers()) {
                for (BankAccount account : user.getAccounts()) {
                    if (accountIban.equals(account.getIban())
                            || accountIban.equals(account.getAlias())) {

                        double convertedAmount = exchangeRates.exchange(input.getCurrency(),
                                account.getCurrency(), splitAmount, new ArrayList<>());
                        if (convertedAmount == -1) {
                            // error
                            return;
                        }

                        if (account.getBalance() < convertedAmount) {
                            incompatibleAccount = account.getIban();
                        }
                    }
                }
            }
        }

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (input.getAccounts().contains(account.getIban())) {
                    if (!incompatibleAccount.isEmpty()) {
                        account.addToTransactionLog(TransactionFactory.createTransaction(input,
                                Map.of("errorIBAN", incompatibleAccount)));
                    } else {
                        double convertedAmount = exchangeRates.exchange(input.getCurrency(),
                                account.getCurrency(), splitAmount, new ArrayList<>());
                        if (convertedAmount == -1) {
                            // error
                            return;
                        }

                        account.withdraw(convertedAmount, true);
                        account.addToTransactionLog(TransactionFactory.createTransaction(input,
                                Map.of("errorIBAN", "")));
                    }
                }
            }
        }
    }
}
