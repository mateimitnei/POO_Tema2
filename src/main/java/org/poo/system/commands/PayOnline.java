package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.ExchangeCurrency;
import org.poo.system.Output;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.cards.Card;
import org.poo.system.transactions.Transaction;
import org.poo.system.transactions.TransactionFactory;

import java.util.ArrayList;
import java.util.Map;

public final class PayOnline implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();
        ExchangeCurrency exchangeRates = new ExchangeCurrency(engine.getInput().getExchangeRates());

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(input.getCardNumber())) {

                        if (card.getStatus().equals("frozen")) {
                            account.addTransaction(new Transaction(input.getTimestamp(),
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
                            account.withdraw(convertedAmount, exchangeRates);

                            account.addTransaction(TransactionFactory.createTransaction(input, Map.of(
                                    "amount", String.valueOf(convertedAmount),
                                    "commerciant", input.getCommerciant(),
                                    "iban", account.getIban()
                            )));

                            card.madePayment(account, user.getEmail(), input.getTimestamp());

                        } catch (ArithmeticException e) { // Exception from account.withdraw()
                            account.addTransaction(new Transaction(input.getTimestamp(),
                                    "Insufficient funds"));
                        }

                        return;
                    }
                }
            }
        }

        // If the card was not found
        ObjectNode commandOutput = engine.getObjectMapper().createObjectNode();
        ObjectNode output = engine.getObjectMapper().createObjectNode();

        output.put("timestamp", input.getTimestamp());
        output.put("description", "Card not found");

        commandOutput.put("command", input.getCommand());
        commandOutput.set("output", output);
        commandOutput.put("timestamp", input.getTimestamp());

        Output.getInstance().getOutput().add(commandOutput);
    }
}
