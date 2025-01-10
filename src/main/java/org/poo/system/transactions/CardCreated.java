package org.poo.system.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class CardCreated extends Transaction {
    private final String cardNumber;
    private final String userEmail;
    private final String account;

    public CardCreated(final int timestamp, final String cardNumber, final String email,
                       final String account) {
        super(timestamp, "New card created");
        this.cardNumber = cardNumber;
        this.userEmail = email;
        this.account = account;
    }

    @Override
    public ObjectNode mappedTransaction(final ObjectMapper objectMapper) {
        ObjectNode output = super.mappedTransaction(objectMapper);
        output.put("card", cardNumber);
        output.put("cardHolder", userEmail);
        output.put("account", account);
        return output;
    }
}
