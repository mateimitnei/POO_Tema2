package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.Transaction;

public final class Report implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        ObjectNode commandOutput = engine.getObjectMapper().createObjectNode();
        ObjectNode output = engine.getObjectMapper().createObjectNode();

        BankAccount accountFound = null;

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (input.getAccount().equals(account.getIban())) {
                    accountFound = account;
                    break;
                }
            }
        }

        if (accountFound == null) {
            output.put("timestamp", input.getTimestamp());
            output.put("description", "Account not found");

        } else {
            ArrayNode transactionsArray = engine.getObjectMapper().createArrayNode();

            output.put("IBAN", accountFound.getIban());
            output.put("balance", accountFound.getBalance());
            output.put("currency", accountFound.getCurrency());

            for (Transaction transaction : accountFound.getTransactionsLog()) {
                if (transaction.getTimestamp() >= input.getStartTimestamp()
                        && transaction.getTimestamp() <= input.getEndTimestamp()) {

                    if (accountFound.getAccountType().equals("savings")
                            && !transaction.hasInterest()) {
                        continue;
                    }

                    transactionsArray.add(
                            transaction.mappedTransaction(engine.getObjectMapper())
                    );
                }
            }

            output.set("transactionsLog", transactionsArray);
        }

        commandOutput.put("command", input.getCommand());
        commandOutput.set("output", output);
        commandOutput.put("timestamp", input.getTimestamp());

        Output.getInstance().getOutput().add(commandOutput);
    }
}
