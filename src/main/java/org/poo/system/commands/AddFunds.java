package org.poo.system.commands;

import org.poo.fileio.CommandInput;
import org.poo.system.ExchangeCurrency;
import org.poo.system.accounts.BankAccount;
import org.poo.system.Engine;
import org.poo.system.User;
import org.poo.system.accounts.BusinessAccount;

import java.util.ArrayList;

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

                            System.out.println("    BUSINESS: " + account.getIban() + ", " + user.getEmail());
                            System.out.println("    Balance: " + account.getBalance());
                            ExchangeCurrency exchanger = ExchangeCurrency.getInstance();
                            double converted = exchanger.exchange("RON", account.getCurrency(),
                                    ((BusinessAccount) account).getDepositLimit(), new ArrayList<>());
                            System.out.println("    Limit: " + converted);

                            ((BusinessAccount) account).deposit(input.getAmount(), associate,
                                    input.getTimestamp());

                            System.out.println("    Deposit: " + input.getAmount() + ", "
                                    + ((BusinessAccount) account).getRoles().get(associate.getEmail())
                                    + ": " + associate.getEmail());
                            System.out.println("    Balance: " + account.getBalance() + "\n");
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
