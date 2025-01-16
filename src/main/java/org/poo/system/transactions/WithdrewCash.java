package org.poo.system.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class WithdrewCash extends Transaction {
    private final double amount;

    public WithdrewCash(final int timestamp, final double amount) {
        super(timestamp, "Cash withdrawal of " + amount);
        this.amount = amount;
    }

    @Override
    public ObjectNode mappedTransaction(final ObjectMapper objectMapper) {
        ObjectNode output = super.mappedTransaction(objectMapper);
        output.put("amount", amount);
        return output;
    }
}
