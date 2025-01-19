package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.Transaction;

public final class DeleteAccount implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        ObjectNode commandOutput = engine.getObjectMapper().createObjectNode();
        ObjectNode output = engine.getObjectMapper().createObjectNode();

        User owner = null;
        String result = "";
        for (User user : engine.getUsers()) {
            if (user.getEmail().equals(input.getEmail())) {
                result = user.deleteAccount(input.getAccount());
                owner = user;
                break;
            }
        }

        if (result.equals("deleted")) {
            output.put("success", "Account deleted");
        } else {
            output.put("error",
                    "Account couldn't be deleted - see org.poo.transactions for details");
        }

        if (result.equals("has money")) {
            try {
                for (BankAccount account : owner.getAccounts()) {
                    if (account.getIban().equals(input.getAccount())) {
                        account.addToTransactionLog(new Transaction(input.getTimestamp(),
                                "Account couldn't be deleted - there are funds remaining"));
                        break;
                    }
                }
            } catch (NullPointerException e) {
                // Not good
            }
        }

        output.put("timestamp", input.getTimestamp());

        commandOutput.put("command", input.getCommand());
        commandOutput.set("output", output);
        commandOutput.put("timestamp", input.getTimestamp());

        Output.getInstance().getOutput().add(commandOutput);
    }
}
