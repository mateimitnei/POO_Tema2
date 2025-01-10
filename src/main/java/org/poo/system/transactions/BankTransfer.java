package org.poo.system.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class BankTransfer extends Transaction {
    private final String senderIBAN;
    private final String receiverIBAN;
    private final String amount;
    private final String transferType;

    public BankTransfer(final int timestamp, final String description, final String from,
                        final String to, final double amount, final String currency,
                        final String type) {
        super(timestamp, description);
        this.senderIBAN = from;
        this.receiverIBAN = to;
        this.amount = amount + " " + currency;
        this.transferType = type;
    }

    @Override
    public ObjectNode mappedTransaction(final ObjectMapper objectMapper) {
        ObjectNode output = super.mappedTransaction(objectMapper);
        output.put("senderIBAN", senderIBAN);
        output.put("receiverIBAN", receiverIBAN);
        output.put("amount", amount);
        output.put("transferType", transferType);
        return output;
    }
}
