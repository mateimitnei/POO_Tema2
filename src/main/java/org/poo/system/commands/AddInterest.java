package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.TheNotFoundError;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.accounts.SavingsAccount;
import org.poo.system.transactions.TransactionFactory;

import java.util.Map;

public final class AddInterest implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {
                    if (account.getAccountType().equals("savings")) {

                        double interest =
                                account.getBalance() * ((SavingsAccount) account).getInterestRate();
                        account.deposit(interest);

                        account.addToTransactionLog(TransactionFactory.createTransaction(input,
                                Map.of(
                                        "amount", String.valueOf(interest),
                                        "currency", account.getCurrency()
                                )));
                        return;
                    }

                    // If the account is not a savings account
                    ObjectNode commandOutput = TheNotFoundError
                            .makeOutput(input, engine.getObjectMapper(),
                                    "This is not a savings account");

                    Output.getInstance().getOutput().add(commandOutput);
                    return;
                }
            }
        }
    }
}
