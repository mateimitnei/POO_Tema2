package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.TheNotFoundError;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.accounts.BusinessAccount;

import java.util.Map;

public class NewBusinessAssociate implements Strategy {

    @Override
    public void execute(CommandInput input) {
        Engine engine = Engine.getInstance();

        User associate = engine.getUsers().stream()
                .filter(u -> u.getEmail().equals(input.getEmail()))
                .findFirst().orElse(null);

        if (associate == null) {
            // If the user is not found
            ObjectNode commandOutput = TheNotFoundError
                    .makeOutput(input, engine.getObjectMapper(), "User not found");

            Output.getInstance().getOutput().add(commandOutput);
            return;
        }

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())
                        && account.getAccountType().equals("business")) {

                    if (((BusinessAccount) account).getAssociates().contains(associate)
                            || user == associate) {
                        // If the user is already an associate or the owner
                        ObjectNode commandOutput = TheNotFoundError
                                .makeOutput(input, engine.getObjectMapper(),
                                        "The user is already an associate of the account.");

                        Output.getInstance().getOutput().add(commandOutput);
                        return;
                    }

                    ((BusinessAccount) account).addAssociate(associate, input.getRole());
                    return;
                }
            }
        }
    }
}
