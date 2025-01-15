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

public final class SendMoney implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();
        ExchangeCurrency exchangeRates = ExchangeCurrency.getInstance();

        BankAccount senderAccount = null;
        BankAccount receiverAccount = null;

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {
                    senderAccount = account;
                }
                if (account.getIban().equals(input.getReceiver())
                        || account.getAlias().equals(input.getReceiver())) {
                    receiverAccount = account;
                }
            }
        }

        try {
            double convertedAmount = exchangeRates.exchange(senderAccount.getCurrency(),
                    receiverAccount.getCurrency(), input.getAmount(), new ArrayList<>());
            if (convertedAmount == -1) {
                // error
                return;
            }

            senderAccount.withdraw(input.getAmount());
            senderAccount.addToTransactionLog(TransactionFactory.createTransaction(input, Map.of(
                    "currency", senderAccount.getCurrency(),
                    "type", "sent",
                    "amount", String.valueOf(input.getAmount())
            )));

            receiverAccount.deposit(convertedAmount);
            receiverAccount.addToTransactionLog(TransactionFactory.createTransaction(input, Map.of(
                    "currency", receiverAccount.getCurrency(),
                    "type", "received",
                    "amount", String.valueOf(convertedAmount)
            )));

            // System.out.println("SendMoney:");

        } catch (ArithmeticException e) { // Exception from senderAccount.withdraw()
            senderAccount.addToTransactionLog(new Transaction(input.getTimestamp(),
                    "Insufficient funds"));
        } catch (NullPointerException e) {
            // If the account was not found, do nothing
        }
    }
}
