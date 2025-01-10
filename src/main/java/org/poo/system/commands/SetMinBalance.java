package org.poo.system.commands;

import org.poo.fileio.CommandInput;
import org.poo.system.accounts.BankAccount;
import org.poo.system.Engine;
import org.poo.system.User;

public final class SetMinBalance implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {
                    account.setMinBalance(input.getAmount());
                    return;
                }
            }
        }
    }
}
