package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.*;
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

        User senderUser = null;
        User receiverUser = null;
        BankAccount senderAccount = null;
        BankAccount receiverAccount = null;

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {
                    senderUser = user;
                    senderAccount = account;
                }
                if (account.getIban().equals(input.getReceiver())
                        || (account.getAlias().equals(input.getReceiver())
                        && !account.getAlias().isEmpty())) {
                    receiverUser = user;
                    receiverAccount = account;
                }
            }
        }

        if (senderUser == null) {
            // If the user was not found
            ObjectNode commandOutput = TheNotFoundError
                    .makeOutput(input, engine.getObjectMapper(), "User not found");

            Output.getInstance().getOutput().add(commandOutput);
        }

        Commerciant commerciant = CommerciantList.getInstance().getCommerciants()
                .stream().filter(c -> c.getAccount().equals(input.getReceiver()))
                .findFirst().orElse(null);

        if (commerciant != null) {
            sendToCommerciant(commerciant, senderAccount, input);
            return;
        }

        if (receiverUser == null) {
            // If the user was not found
            ObjectNode commandOutput = TheNotFoundError
                    .makeOutput(input, engine.getObjectMapper(), "User not found");

            Output.getInstance().getOutput().add(commandOutput);
            return;
        }

        try {
            double convertedAmount = exchangeRates.exchange(senderAccount.getCurrency(),
                    receiverAccount.getCurrency(), input.getAmount(), new ArrayList<>());
            if (convertedAmount == -1) {
                // error
                return;
            }

            senderAccount.withdraw(input.getAmount(), true);
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

    private void sendToCommerciant(final Commerciant commerciant, final BankAccount senderAccount,
                                   final CommandInput input) {
        try {
            senderAccount.withdraw(input.getAmount(), true);
            senderAccount.addToTransactionLog(TransactionFactory.createTransaction(input, Map.of(
                    "currency", senderAccount.getCurrency(),
                    "type", "sent",
                    "amount", String.valueOf(input.getAmount())
            )));

            senderAccount.applyCashback(commerciant, input.getAmount());
            System.out.println("Send money to commerciant \n");

        } catch (ArithmeticException e) { // Exception from senderAccount.withdraw()
            senderAccount.addToTransactionLog(new Transaction(input.getTimestamp(),
                    "Insufficient funds"));

        } catch (NullPointerException e) {
            // If the account was not found, do nothing
        }
    }
}
