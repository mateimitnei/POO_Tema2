package org.poo.system.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;
import java.util.Locale;

public final class PayedWithOthers extends Transaction {
    private final String currency;
    private final double amount;
    private final List<String> accounts;
    private final String errorAccount;

    public PayedWithOthers(final int timestamp, final List<String> accounts, final double amount,
                           final String currency, final String errorAccount) {
        super(timestamp, "Split payment of "
                + String.format(Locale.US, "%.2f", amount) + " " + currency);
        this.accounts = accounts;
        this.amount = amount;
        this.currency = currency;
        this.errorAccount = errorAccount;
    }

    @Override
    public ObjectNode mappedTransaction(final ObjectMapper objectMapper) {
        ObjectNode output = super.mappedTransaction(objectMapper);
        output.put("currency", currency);
        output.put("amount", amount / accounts.size());
        output.set("involvedAccounts", objectMapper.valueToTree(accounts));

        if (!errorAccount.isEmpty()) {
            output.put("error", "Account " + errorAccount
                    + " has insufficient funds for a split payment.");
        }

        return output;
    }
}
