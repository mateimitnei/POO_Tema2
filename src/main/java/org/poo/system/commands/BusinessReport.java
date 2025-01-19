package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.TheNotFoundError;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.accounts.BusinessAccount;
import org.poo.system.transactions.Transaction;

public final class BusinessReport implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {
                    if (account.getAccountType().equals("business")) {
                        ObjectNode commandOutput = engine.getObjectMapper().createObjectNode();

                        ObjectNode output = new ObjectNode(null);

                        if (input.getType().equals("transaction")) {
                            output = ((BusinessAccount) account).mappedTransactionReport(
                                    engine.getObjectMapper(),
                                    input.getStartTimestamp(),
                                    input.getEndTimestamp()
                            );
                        } else if (input.getType().equals("commerciant")) {
                            output = ((BusinessAccount) account).mappedCommerciantReport(
                                    engine.getObjectMapper(),
                                    input.getStartTimestamp(),
                                    input.getEndTimestamp()
                            );
                        }

                        commandOutput.put("command", input.getCommand());
                        commandOutput.set("output", output);
                        commandOutput.put("timestamp", input.getTimestamp());

                        Output.getInstance().getOutput().add(commandOutput);

                        return;
                    }

                    account.addToTransactionLog(new Transaction(input.getTimestamp(),
                            "Account is not of type business"));
                    return;
                }
            }
        }

        // If the account was not found
        ObjectNode commandOutput = TheNotFoundError
                .makeOutput(input, engine.getObjectMapper(), "Account not found");

        Output.getInstance().getOutput().add(commandOutput);
    }
}
