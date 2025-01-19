package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.TheNotFoundError;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.accounts.BusinessAccount;

public class ChangeSpendingLimit implements Strategy {

    @Override
    public void execute(CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())
                        && account.getAccountType().equals("business")) {

                    User owner = engine.getUsers().stream()
                            .filter(u -> u.getEmail().equals(input.getEmail()))
                            .findFirst().orElse(null);

                    if (user == owner) {
                        ((BusinessAccount) account).setSpendingLimit(input.getAmount());
                    } else {
                        // If the user is not the owner of the account
                        ObjectNode commandOutput = TheNotFoundError
                                .makeOutput(input, engine.getObjectMapper(),
                                        "You must be owner in order to change spending limit.");

                        Output.getInstance().getOutput().add(commandOutput);
                    }

                    return;
                }
            }
        }

        //If the user is not found
        ObjectNode commandOutput = TheNotFoundError
                .makeOutput(input, engine.getObjectMapper(), "User not found");

        Output.getInstance().getOutput().add(commandOutput);
    }
}
