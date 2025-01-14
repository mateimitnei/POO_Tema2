package org.poo.system.accounts;

import org.poo.fileio.CommandInput;
import org.poo.system.User;

public final class AccountFactory {

    private AccountFactory() { }

    /**
     * Creates a new bank account based on the input.
     *
     * @param input the command input with the account type and other details
     * @return the created bank account
     */
    public static BankAccount createAccount(final CommandInput input, final User owner) {
        return switch (input.getAccountType()) {
            case "classic" -> new ClassicAccount(input.getCurrency(), owner);
            case "savings" -> new SavingsAccount(input.getCurrency(),
                    input.getInterestRate(), owner);
            case "business" -> new BusinessAccount(input.getCurrency(), owner);
            default -> throw new IllegalArgumentException("Invalid account type.");
        };
    }
}
