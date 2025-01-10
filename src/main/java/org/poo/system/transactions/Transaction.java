package org.poo.system.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;

/**
 * TO BE EXTENDED.
 * Represents a transaction.
 */
@Getter @Setter
public class Transaction {
    private int timestamp;
    private String description;

    public Transaction(final int timestamp, final String description) {
        this.timestamp = timestamp;
        this.description = description;
    }

    /**
     * Checks if it's an interest related transaction.
     * To be overridden by interest related transactions.
     *
     * @return false by default
     */
    public boolean hasInterest() {
        return false;
    }

    /**
     * TO BE OVERRIDDEN.
     * Maps the transaction to a JSON object.
     *
     * @param objectMapper the object mapper
     * @return the JSON object
     */
    public ObjectNode mappedTransaction(final ObjectMapper objectMapper) {
        ObjectNode output = objectMapper.createObjectNode();
        output.put("timestamp", timestamp);
        output.put("description", description);
        return output;
    }
}
