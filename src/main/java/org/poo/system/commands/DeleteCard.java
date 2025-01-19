package org.poo.system.commands;

import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.accounts.BusinessAccount;
import org.poo.system.cards.Card;
import org.poo.system.transactions.TransactionFactory;

import java.util.Map;

public final class DeleteCard implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                for (Card card : account.getCards()) {
                    if (card.getCardNumber().equals(input.getCardNumber())) {

                        if (account.getAccountType().equals("business") &&
                            (!((BusinessAccount) account).getRoles().containsKey(input.getEmail()) ||
                            (((BusinessAccount) account).getRoles().get(input.getEmail()).equals("employee") &&
                            !card.getCreator().equals(input.getEmail())))) {
                                return;
                            }

                        account.getCards().remove(card);

                        account.addToTransactionLog(TransactionFactory.createTransaction(input,
                                Map.of("account", account.getIban())));
                        break;
                    }
                }
            }
        }
    }
}
