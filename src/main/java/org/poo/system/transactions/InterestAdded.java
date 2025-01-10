package org.poo.system.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class InterestAdded extends Transaction {

    public InterestAdded(final int timestamp) {
        super(timestamp, "Interest added");
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
