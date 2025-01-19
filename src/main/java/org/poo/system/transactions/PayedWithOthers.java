package org.poo.system.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class PayedWithOthers extends Transaction {
    private final String currency;
    private final double amount;
    private final List<Double> amounts;
    private final String type;
    private final List<String> accounts;
    private final String errorAccount;
    private final String rejected;

    public PayedWithOthers(final CommandInput input, final String errorAccount,
                           final String rejected) {

        super(input.getTimestamp(), "Split payment of "
                + String.format(Locale.US, "%.2f", input.getAmount()) + " " + input.getCurrency());

        this.accounts = input.getAccounts();
        this.amount = input.getAmount();
        if (input.getAmountForUsers() != null) {
            this.amounts = new ArrayList<>(input.getAmountForUsers());
        } else {
            this.amounts = null;
        }
        this.type = input.getSplitPaymentType();
        this.currency = input.getCurrency();
        this.errorAccount = errorAccount;
        this.rejected = rejected;
    }

    @Override
    public ObjectNode mappedTransaction(final ObjectMapper objectMapper) {
        ObjectNode output = super.mappedTransaction(objectMapper);
        output.put("splitPaymentType", type);
        output.put("currency", currency);

        if (type.equals("custom")) {
            output.set("amountForUsers", objectMapper.valueToTree(amounts));
        } else {
            output.put("amount", amount / accounts.size());
        }

        output.set("involvedAccounts", objectMapper.valueToTree(accounts));

        if (!errorAccount.isEmpty()) {
            output.put("error", "Account " + errorAccount
                    + " has insufficient funds for a split payment.");
        } else if (!rejected.isEmpty()) {
            output.put("error", "One user rejected the payment.");
        }

        return output;
    }
}
