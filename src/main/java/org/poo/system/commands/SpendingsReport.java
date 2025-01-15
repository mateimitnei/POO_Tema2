package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.CardPayment;
import org.poo.system.transactions.Transaction;

import java.util.*;

public final class SpendingsReport implements Strategy {
    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();

        ObjectNode commandOutput = engine.getObjectMapper().createObjectNode();
        ObjectNode output = engine.getObjectMapper().createObjectNode();

        BankAccount accountFound = null;
        boolean savingsAccount = false;

        for (User user : engine.getUsers()) {
            for (BankAccount account : user.getAccounts()) {
                if (input.getAccount().equals(account.getIban())) {
                    accountFound = account;

                    if (account.getAccountType().equals("savings")) {
                        savingsAccount = true;

                        output.put("error",
                                "This kind of report is not supported for a saving account");
                    }
                    break;
                }
            }
        }

        if (accountFound == null) {
            output.put("timestamp", input.getTimestamp());
            output.put("description", "Account not found");

        } else if (!savingsAccount) {
            ArrayNode transactionsArray = engine.getObjectMapper().createArrayNode();
            ArrayNode commerciantsArray = engine.getObjectMapper().createArrayNode();

            Map<String, Double> commerciantsMap = new HashMap<>();

            for (Transaction transaction : accountFound.getTransactionsLog()) {
                if (transaction.getDescription().equals("Card payment")
                        && transaction.getTimestamp() >= input.getStartTimestamp()
                        && transaction.getTimestamp() <= input.getEndTimestamp()) {

                    transactionsArray.add(
                            transaction.mappedTransaction(engine.getObjectMapper())
                    );

                    CardPayment cardPayment = (CardPayment) transaction;

                    commerciantsMap.put(cardPayment.getCommerciant(),
                            commerciantsMap.getOrDefault(cardPayment.getCommerciant(), 0.0)
                                    + cardPayment.getAmount());
                }
            }

            List<String> sortedCommerciants = new ArrayList<>(commerciantsMap.keySet());
            Collections.sort(sortedCommerciants);

            for (String commerciant : sortedCommerciants) {
                ObjectNode commerciantNode = engine.getObjectMapper().createObjectNode();
                commerciantNode.put("commerciant", commerciant);
                commerciantNode.put("total", commerciantsMap.get(commerciant));
                commerciantsArray.add(commerciantNode);
            }

            output.put("IBAN", accountFound.getIban());
            output.put("balance", accountFound.getBalance());
            output.put("currency", accountFound.getCurrency());

            output.set("transactionsLog", transactionsArray);
            output.set("commerciants", commerciantsArray);
        }

        commandOutput.put("command", input.getCommand());
        commandOutput.set("output", output);
        commandOutput.put("timestamp", input.getTimestamp());

        Output.getInstance().getOutput().add(commandOutput);
    }
}
