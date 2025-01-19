package org.poo.system.commands;

import org.poo.fileio.CommandInput;
import org.poo.system.accounts.BankAccount;
import org.poo.system.accounts.BusinessAccount;
import org.poo.system.cards.CardFactory;
import org.poo.system.Engine;
import org.poo.system.User;
import org.poo.system.transactions.TransactionFactory;
import org.poo.utils.Utils;

import java.util.Map;

public final class CreateCard implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (account.getIban().equals(input.getAccount())) {
                    String cardNumber = Utils.generateCardNumber();

                    if (account.getAccountType().equals("business")
                            && !((BusinessAccount) account).getRoles().containsKey(input.getEmail())
                            && !user.getEmail().equals(input.getEmail())) {
                        return;
                    }

                    account.addCard(CardFactory.createCard(input.getCommand(),
                            account.getIban(), cardNumber, input.getEmail()));

                    account.addToTransactionLog(TransactionFactory.createTransaction(input,
                            Map.of("cardNumber", cardNumber)));

                    System.out.println("    CARD CREATED by " + input.getEmail() + " for " + account.getIban() + "\n");
                    return;
                }
            }
        }
    }
}
