package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.*;
import org.poo.system.accounts.BankAccount;
import org.poo.system.accounts.BusinessAccount;
import org.poo.system.cards.Card;
import org.poo.system.transactions.Transaction;
import org.poo.system.transactions.TransactionFactory;

import java.util.ArrayList;
import java.util.Map;

public final class PayOnline implements Strategy {
    @Override
    public void execute(final CommandInput input) {

        if (input.getAmount() <= 0) {
            return;
        }

        Engine engine = Engine.getInstance();

        Commerciant commerciant = CommerciantList.getInstance().getCommerciants()
                    .stream().filter(c -> c.getName().equals(input.getCommerciant()))
                    .findFirst().orElse(null);

        if (commerciant == null) {
            // If the commerciant was not found
            ObjectNode commandOutput = TheNotFoundError
                    .makeOutput(input, engine.getObjectMapper(), "Commerciant not found");

            Output.getInstance().getOutput().add(commandOutput);
            return;
        }

        ExchangeCurrency exchangeRates = ExchangeCurrency.getInstance();

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(input.getCardNumber())) {

                        if (card.getStatus().equals("frozen")) {
                            account.addToTransactionLog(new Transaction(input.getTimestamp(),
                                    "The card is frozen"));
                            return;
                        }

                        try {
                            double convertedAmount = exchangeRates.exchange(input.getCurrency(),
                                    account.getCurrency(), input.getAmount(), new ArrayList<>());
                            if (convertedAmount == -1) {
                                // error
                                return;
                            }

                            if (account.getAccountType().equals("business")) {

                                User associate = engine.getUsers().stream()
                                        .filter(u -> u.getEmail().equals(input.getEmail()))
                                        .findFirst().orElse(null);

                                if (((BusinessAccount) account).getAssociates().contains(associate)
                                        && associate != null) {
                                    ((BusinessAccount) account).withdraw(convertedAmount,
                                            true, associate, input.getTimestamp());
                                }

                            } else {
                                account.withdraw(convertedAmount, true);
                            }

                            System.out.println("    PayOnline: " + convertedAmount + " " + account.getCurrency() + ", " + user.getEmail());
                            System.out.println("    Plan: " + user.getPlan() + "\n");
                            account.applyCashback(commerciant, convertedAmount);

                            account.addToTransactionLog(TransactionFactory.createTransaction(input, Map.of(
                                    "amount", String.valueOf(convertedAmount),
                                    "commerciant", input.getCommerciant(),
                                    "iban", account.getIban()
                            )));

                            card.madePayment(account, user.getEmail(), input.getTimestamp());

                        } catch (ArithmeticException e) { // Exception from account.withdraw()
                            account.addToTransactionLog(new Transaction(input.getTimestamp(),
                                    "Insufficient funds"));
                        }

                        return;
                    }
                }
            }
        }

        // If the card was not found
        ObjectNode commandOutput = TheNotFoundError
                .makeOutput(input, engine.getObjectMapper(), "Card not found");

        Output.getInstance().getOutput().add(commandOutput);
    }
}
