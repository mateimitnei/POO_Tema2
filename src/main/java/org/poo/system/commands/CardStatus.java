package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.cards.Card;
import org.poo.system.transactions.Transaction;

public final class CardStatus implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(input.getCardNumber())) {

                        if (card.getStatus().equals("active")
                                && account.checkStatus(card.getCardNumber()).equals("frozen")) {
                            account.addToTransactionLog(new Transaction(input.getTimestamp(),
                                    "You have reached the minimum amount of funds, "
                                            + "the card will be frozen"));
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
