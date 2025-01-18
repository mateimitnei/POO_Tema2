package org.poo.system.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.system.ExchangeCurrency;
import org.poo.system.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter @Setter
public final class BusinessAccount extends BankAccount {
    private List<User> associates;
    private Map<String, String> roles;
    private Map<String, Double> spendings;
    private Map<Integer, Map<String, Double>> spendingsHistory;
    private Map<String, Double> deposits;
    private Map<Integer, Map<String, Double>> depositsHistory;
    private double depositLimit;
    private double spendingLimit;

    public BusinessAccount(final String currency, final User owner) {
        super(currency, owner);
        associates = new ArrayList<>();
        roles = new HashMap<>();
        spendings = new HashMap<>();
        spendingsHistory = new HashMap<>();
        deposits = new HashMap<>();
        depositsHistory = new HashMap<>();
        depositLimit = 500.0;
        spendingLimit = 500.0;
    }

    public void addAssociate(final User associate, final String role) {
        associates.add(associate);
        roles.put(associate.getEmail(), role);
    }

    public void withdraw(final double amount, final boolean isPayment, final User user, final int timestamp) {
        ExchangeCurrency exchanger = ExchangeCurrency.getInstance();
        double amountInLei = exchanger.exchange(currency, "RON", amount, new ArrayList<>());
        if (roles.get(user.getEmail()).equals("employee") && amountInLei > spendingLimit) {
            return;
        }
        super.withdraw(amount, isPayment);
        spendings.put(user.getEmail(), spendings.getOrDefault(user.getEmail(), 0.0) + amount);
        spendingsHistory.put(timestamp, spendings);
    }

    public void deposit(final double amount, final User user, final int timestamp) {
        ExchangeCurrency exchanger = ExchangeCurrency.getInstance();
        double amountInLei = exchanger.exchange(currency, "RON", amount, new ArrayList<>());
        if (roles.get(user.getEmail()).equals("employee") && amountInLei > depositLimit) {
            return;
        }
        super.deposit(amount);
        deposits.put(user.getEmail(), deposits.getOrDefault(user.getEmail(), 0.0) + amount);
        depositsHistory.put(timestamp, deposits);
    }

    public String getAccountType() {
        return "business";
    }

    public ObjectNode mappedTransactionReport(final ObjectMapper objectMapper,
                                              final int start, final int end) {
        ObjectNode output = objectMapper.createObjectNode();
        output.put("IBAN", iban);
        output.put("balance", balance);
        output.put("currency", currency);
        output.put("spending limit", spendingLimit);
        output.put("deposit limit", depositLimit);
        output.put("statistics type", "transaction");
        double totalSpent = 0.0;
        double totalDeposited = 0.0;
        ArrayNode managersArray = objectMapper.createArrayNode();
        for (User user : associates) {
            if (roles.get(user.getEmail()).equals("manager")) {
                managersArray.add(mappedAssociate(objectMapper, user,
                        totalSpent, totalDeposited, start, end));
            }
        }
        ArrayNode employeesArray = objectMapper.createArrayNode();
        for (User user : associates) {
            if (roles.get(user.getEmail()).equals("employee")) {
                employeesArray.add(mappedAssociate(objectMapper, user,
                        totalSpent, totalDeposited, start, end));
            }
        }
        output.set("managers", managersArray);
        output.set("employees", employeesArray);
        output.put("total spent", totalSpent);
        output.put("total deposited", totalDeposited);
        return output;
    }

    private ObjectNode mappedAssociate(final ObjectMapper objectMapper, final User user,
                                       double totalSpent, double totalDeposited,
                                       final int start, final int end) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("username", user.getLastName() + " " + user.getFirstName());
        double spent = spentInInterval(user.getEmail(), start, end);
        totalSpent += spent;
        userNode.put("spent", spent);
        double deposited = depositedInInterval(user.getEmail(), start, end);
        totalDeposited += deposited;
        userNode.put("deposited", deposited);
        return userNode;
    }

    public ObjectNode mappedCommerciantReport(final ObjectMapper objectMapper) {
        ObjectNode output = objectMapper.createObjectNode();
        return null;
    }

    private double spentInInterval(final String email, final int start, final int end) {
        return spendingsHistory.entrySet().stream()
                .filter(timestamp -> timestamp.getKey() >= start && timestamp.getKey() <= end)
                .flatMap(timestamp -> timestamp.getValue().entrySet().stream())
                .filter(spent -> spent.getKey().equals(email))
                .mapToDouble(Map.Entry::getValue)
                .sum();
    }

    private double depositedInInterval(final String email, final int start, final int end) {
        return depositsHistory.entrySet().stream()
                .filter(timestamp -> timestamp.getKey() >= start && timestamp.getKey() <= end)
                .flatMap(timestamp -> timestamp.getValue().entrySet().stream())
                .filter(deposited -> deposited.getKey().equals(email))
                .mapToDouble(Map.Entry::getValue)
                .sum();
    }
}
