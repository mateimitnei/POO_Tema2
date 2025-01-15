package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.accounts.SavingsAccount;
import org.poo.system.transactions.TransactionFactory;

public final class ChangeInterestRate implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {
                    if (account.getAccountType().equals("savings")) {
                        ((SavingsAccount) account).setInterestRate(input.getInterestRate());
                        account.addToTransactionLog(TransactionFactory.createTransaction(input, null));
                        return;
                    }

                    // If the account is not a savings account
                    ObjectNode commandOutput = engine.getObjectMapper().createObjectNode();
                    ObjectNode output = engine.getObjectMapper().createObjectNode();

                    output.put("timestamp", input.getTimestamp());
                    output.put("description", "This is not a savings account");

                    commandOutput.put("command", input.getCommand());
                    commandOutput.set("output", output);
                    commandOutput.put("timestamp", input.getTimestamp());

                    Output.getInstance().getOutput().add(commandOutput);
                    return;
                }
            }
        }
    }
}
