package org.poo.system.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

@Getter
public final class CardPayment extends Transaction {
    private final double amount;
    private final String commerciant;
    private final String accountIban;

    public CardPayment(final int timestamp, final double amount, final String commerciant,
                       final String iban) {
        super(timestamp, "Card payment");
        this.amount = amount;
        this.commerciant = commerciant;
        accountIban = iban;
    }

    @Override
    public ObjectNode mappedTransaction(final ObjectMapper objectMapper) {
        ObjectNode output = super.mappedTransaction(objectMapper);
        output.put("amount", amount);
        output.put("commerciant", commerciant);
        return output;
    }
}
