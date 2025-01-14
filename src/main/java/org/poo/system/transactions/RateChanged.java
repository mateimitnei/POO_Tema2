package org.poo.system.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class RateChanged extends Transaction {

    public RateChanged(final int timestamp, final double rate) {
        super(timestamp, "Interest rate of the account changed to " + rate);
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
        return output;
    }
}
