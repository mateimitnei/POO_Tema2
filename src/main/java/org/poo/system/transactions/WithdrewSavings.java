package org.poo.system.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class WithdrewSavings extends Transaction {
    private final double amount;
    private final String savingsIBAN;
    private final String classicIBAN;

    public WithdrewSavings(final int timestamp, final String savingsIBAN, final double amount,
                           final String classicIBAN) {
        super(timestamp, "Savings withdrawal");
        this.amount = amount;
        this.savingsIBAN = savingsIBAN;
        this.classicIBAN = classicIBAN;
    }

    @Override
    public ObjectNode mappedTransaction(final ObjectMapper objectMapper) {
        ObjectNode output = super.mappedTransaction(objectMapper);
        output.put("amount", amount);
        output.put("classicAccountIBAN", classicIBAN);
        output.put("savingsAccountIBAN", savingsIBAN);
        return output;
    }
}
