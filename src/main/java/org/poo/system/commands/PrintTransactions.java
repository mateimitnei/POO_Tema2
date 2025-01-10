package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class PrintTransactions implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            if (user.getEmail().equals(input.getEmail())) {
                ObjectNode commandOutput = engine.getObjectMapper().createObjectNode();
                ArrayNode transactionsArray = engine.getObjectMapper().createArrayNode();

                List<Transaction> allTransactions = new ArrayList<>();

                for (BankAccount account : user.getAccounts()) {
                    allTransactions.addAll(account.getTransactions());
                }

                allTransactions.sort(Comparator.comparing(Transaction::getTimestamp));

                for (Transaction transaction : allTransactions) {
                    transactionsArray.add(
                            transaction.mappedTransaction(engine.getObjectMapper())
                    );
                }

                commandOutput.put("command", input.getCommand());
                commandOutput.set("output", transactionsArray);
                commandOutput.put("timestamp", input.getTimestamp());

                Output.getInstance().getOutput().add(commandOutput);
                return;
            }
        }
    }
}
