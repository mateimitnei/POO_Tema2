package org.poo.system.commands;

import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;

public final class SetAlias implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            if (user.getEmail().equals(input.getEmail())) {
                for (BankAccount account : user.getAccounts()) {
                    if (account.getIban().equals(input.getAccount())) {
                        account.setAlias(input.getAlias());
                        return;
                    }
                }
            }
        }
    }
}
