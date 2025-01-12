package org.poo.system.commands;

import org.poo.fileio.CommandInput;
import org.poo.system.accounts.AccountFactory;
import org.poo.system.Engine;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.Transaction;

public final class AddAccount implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            if (user.getEmail().equals(input.getEmail())) {
                BankAccount account = AccountFactory.createAccount(input);
                user.addAccount(account);
                account.addTransaction(new Transaction(input.getTimestamp(), "New account created"));

                if (user.getPlan().isEmpty()) {
                    if (user.getOccupation().equals("student")) {
                        user.setPlan("student");
                    } else {
                        user.setPlan("standard");
                    }
                }
                return;
            }
        }
    }
}
