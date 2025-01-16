package org.poo.system.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class InterestAdded extends Transaction {
    private final double amount;
    private final String currency;

    public InterestAdded(final int timestamp, final double amount, final String currency) {
        super(timestamp, "Interest rate income");
        this.amount = amount;
        this.currency = currency;
    }

    @Override
    public boolean hasInterest() {
        return true;
    }

    @Override
    public ObjectNode mappedTransaction(final ObjectMapper objectMapper) {
        ObjectNode output = objectMapper.createObjectNode();
        output.put("timestamp", getTimestamp());
        output.put("description", getDescription());
        output.put("amount", amount);
        output.put("currency", currency);
        return output;
    }
}
