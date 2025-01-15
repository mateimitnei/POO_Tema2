package org.poo.system.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PlanUpgraded extends Transaction{
    private final String iban;
    private final String plan;

    public PlanUpgraded(final int timestamp, final String iban,
                        final String plan) {
        super(timestamp, "Upgrade plan");
        this.iban = iban;
        this.plan = plan;
    }

    @Override
    public ObjectNode mappedTransaction(final ObjectMapper objectMapper) {
        ObjectNode output = super.mappedTransaction(objectMapper);
        output.put("accountIBAN", iban);
        output.put("newPlanType", plan);
        return output;
    }
}
