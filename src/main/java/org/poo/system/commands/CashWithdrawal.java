package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.*;
import org.poo.system.accounts.BankAccount;
import org.poo.system.cards.Card;
import org.poo.system.transactions.Transaction;
import org.poo.system.transactions.TransactionFactory;

import java.util.ArrayList;
import java.util.Map;

public final class CashWithdrawal implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            if (user.getEmail().equals(input.getEmail())) {
                for (BankAccount account : user.getAccounts()) {
                    for (Card card : account.getCards()) {
                        if (card.getCardNumber().equals(input.getCardNumber())) {

                            if (card.getStatus().equals("frozen")) {
                                account.addToTransactionLog(new Transaction(input.getTimestamp(),
                                        "The card is frozen"));
                                return;
                            }

                            try {
                                ExchangeCurrency exchanger = ExchangeCurrency.getInstance();
                                double converted = exchanger.exchange("RON", account.getCurrency(),
                                        input.getAmount(), new ArrayList<>());

                                account.withdraw(converted, false);
                                account.addToTransactionLog(TransactionFactory
                                        .createTransaction(input, Map.of(
                                                "amount", String.valueOf(input.getAmount()))));

                                // card.madePayment(account, user.getEmail(), input.getTimestamp());

                                System.out.println("    Cash withdrawal: "
                                        + converted + " " + account.getCurrency());
                                System.out.println("    Balance: "
                                        + account.getBalance() + " " + account.getCurrency());
                                System.out.println("    Timestamp: "
                                        + input.getTimestamp() + "\n");

                            } catch (ArithmeticException e) {
                                account.addToTransactionLog(new Transaction(input.getTimestamp(),
                                        "Insufficient funds"));
                            }

                            return;
                        }
                    }
                }

                // If the card was not found
                ObjectNode commandOutput = TheNotFoundError
                        .makeOutput(input, engine.getObjectMapper(), "Card not found");

                Output.getInstance().getOutput().add(commandOutput);
                return;
            }
        }

        // If the user was not found
        ObjectNode commandOutput = TheNotFoundError
                .makeOutput(input, engine.getObjectMapper(), "User not found");

        Output.getInstance().getOutput().add(commandOutput);
    }
}
