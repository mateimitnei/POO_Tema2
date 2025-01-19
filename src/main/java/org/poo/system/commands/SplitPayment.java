package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.*;
import org.poo.system.accounts.BankAccount;
import org.poo.system.splitPayment.AllPayments;
import org.poo.system.splitPayment.Payment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SplitPayment implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();
        AllPayments payments = AllPayments.getInstance();

        List<Double> amounts;

        if (input.getSplitPaymentType().equals("equal")) {
            amounts = Collections.nCopies(input.getAccounts().size(),
                    input.getAmount() / input.getAccounts().size());
        } else {
            amounts = new ArrayList<>(input.getAmountForUsers());
        }

        List<BankAccount> accounts = new ArrayList<>();

        for (String accountIban : input.getAccounts()) {
            for (User user : engine.getUsers()) {
                for (BankAccount account : user.getAccounts()) {
                    if (accountIban.equals(account.getIban())
                            || accountIban.equals(account.getAlias())) {
                        accounts.add(account);
                    }
                }
            }
        }

        if (accounts.size() != amounts.size()) {
            // If a user was not found
            ObjectNode commandOutput = TheNotFoundError
                    .makeOutput(input, engine.getObjectMapper(), "User not found");

            Output.getInstance().getOutput().add(commandOutput);
            return;
        }

        payments.addPayment(new Payment(accounts, amounts, input));
    }
}
