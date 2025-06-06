package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.TheNotFoundError;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.accounts.BusinessAccount;

public final class ChangeDepositLimit implements Strategy {

    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())
                        && account.getAccountType().equals("business")) {

                    User owner = engine.getUsers().stream()
                            .filter(u -> u.getEmail().equals(input.getEmail()))
                            .findFirst().orElse(null);

                    if (user == owner) {
                        ((BusinessAccount) account).setDepositLimit(input.getAmount());
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
