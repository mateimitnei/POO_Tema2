package org.poo.system.commands;

import org.poo.fileio.CommandInput;
import org.poo.system.accounts.BankAccount;
import org.poo.system.Engine;
import org.poo.system.User;
import org.poo.system.accounts.BusinessAccount;

public final class AddFunds implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {

                    if (account.getAccountType().equals("business")) {

                        User associate = engine.getUsers().stream()
                                .filter(u -> u.getEmail().equals(input.getEmail()))
                                .findFirst().orElse(null);

                        if (((BusinessAccount) account).getAssociates().contains(associate)
                                && associate != null) {
                            ((BusinessAccount) account).deposit(input.getAmount(), associate,
                                    input.getTimestamp());
                            return;
                        }
                    }

                    account.deposit(input.getAmount());
                    return;
                }
            }
        }
    }
}
