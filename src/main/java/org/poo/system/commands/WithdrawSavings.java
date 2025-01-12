package org.poo.system.commands;

import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.ExchangeCurrency;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;

public final class WithdrawSavings implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();
        ExchangeCurrency exchangeRates = new ExchangeCurrency(engine.getInput().getExchangeRates());

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {

                    if (account.getAccountType().equals("savings")) {
                        if (user.getBirthDate().isAfter(LocalDate.now().minusYears(21))) {
                            account.addTransaction(new Transaction(input.getTimestamp(),
                                    "You don't have the minimum age required."));
                            return;
                        }
                        for (BankAccount receiver : user.getAccounts()) {
                            if (receiver.getAccountType().equals("classic")
                                    && receiver.getCurrency().equals(input.getCurrency())) {

                                double convertedAmount = exchangeRates.exchange(
                                        receiver.getCurrency(), account.getCurrency(),
                                        input.getAmount(), new ArrayList<>()
                                );
                                if (convertedAmount == -1) {
                                    // Faulty conversion
                                    return;
                                }
                                try {
                                    account.withdraw(convertedAmount);
                                    receiver.deposit(input.getAmount());
                                    account.addTransaction(new Transaction(input.getTimestamp(),
                                            "Savings withdrawal"));
                                    return;
                                } catch (ArithmeticException e) { // Exception from withdraw()
                                    account.addTransaction(new Transaction(input.getTimestamp(),
                                            "Insufficient funds"));
                                }
                            }
                        }

                    }

                    // If the account is not a savings account
                    account.addTransaction(new Transaction(input.getTimestamp(),
                            "Account is not of type savings."));
                    return;
                }
            }
        }
    }
}
