package org.poo.system.commands;

import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;

public final class WithdrawSavings implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {
                    if (account.getAccountType().equals("savings")) {
                        account.withdraw(input.getAmount());
                        return;
                    }

                    // If the account is not a savings account
                    return;
                }
            }
        }
    }
}
