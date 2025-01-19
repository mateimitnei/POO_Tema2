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
    private Map<String, Map<String, Double>> spentOnCommerciant;
    private double depositLimit;
    private double spendingLimit;
    private double totalSpentInInterval;
    private double totalDepositedInInterval;
    private static final double INITIAL_SPENDING_LIMIT = 500.0;
    private static final double INITIAL_DEPOSIT_LIMIT = 500.0;

    public BusinessAccount(final String currency, final User owner) {
        super(currency, owner);
        associates = new ArrayList<>();
        roles = new HashMap<>();
        spendings = new HashMap<>();
        spendingsHistory = new HashMap<>();
        deposits = new HashMap<>();
        depositsHistory = new HashMap<>();
        ExchangeCurrency exchanger = ExchangeCurrency.getInstance();
        depositLimit = exchanger.exchange("RON", currency, INITIAL_DEPOSIT_LIMIT,
                new ArrayList<>());
        spendingLimit = exchanger.exchange("RON", currency, INITIAL_SPENDING_LIMIT,
                new ArrayList<>());
    }

    /**
        * Add an associate to the business account.
        * @param associate the user to be added as an associate
        * @param role the role of the associate (manager or employee)
     */
    public void addAssociate(final User associate, final String role) {
        associates.add(associate);
        roles.put(associate.getEmail(), role);
    }

    /**
     * A different type of withdraw method for business accounts.
     * It records all the spendings of the associates
     * @param amount the amount to be withdrawn
     * @param isPayment if the withdrawal is a payment
     * @param user the user (associate) that makes the withdrawal
     * @param timestamp the timestamp of the withdrawal
     */
    public void withdraw(final double amount, final boolean isPayment, final User user,
                         final int timestamp) {
        ExchangeCurrency exchanger = ExchangeCurrency.getInstance();
        double amountInLei = exchanger.exchange(currency, "RON", amount, new ArrayList<>());
        if (roles.get(user.getEmail()).equals("employee") && applyFee(amountInLei) > spendingLimit) {
            return;
        }
        super.withdraw(amount, isPayment);
        spendings.put(user.getEmail(), spendings.getOrDefault(user.getEmail(), 0.0) + amount);
        spendingsHistory.put(timestamp, spendings);
    }

    /**
     * A different type of deposit method for business accounts.
     * It records all the deposits of the associates
     * @param amount the amount to be deposited
     * @param user the user (associate) that makes the deposit
     * @param timestamp the timestamp of the deposit
     */
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

    /**
     * Generates a transaction report for the business account.
     * @param objectMapper the object mapper used to create the JSON object
     * @param start the start timestamp of the transactions
     * @param end the end timestamp of the transactions
     * @return the JSON object representing the report
     */
    public ObjectNode mappedTransactionReport(final ObjectMapper objectMapper,
                                              final int start, final int end) {
        ObjectNode output = objectMapper.createObjectNode();
        output.put("IBAN", iban);
        output.put("balance", balance);
        output.put("currency", currency);
        output.put("spending limit", spendingLimit);
        output.put("deposit limit", depositLimit);
        output.put("statistics type", "transaction");

        totalSpentInInterval = 0.0;
        totalDepositedInInterval = 0.0;
        ArrayNode managersArray = objectMapper.createArrayNode();
        for (User user : associates) {
            if (roles.get(user.getEmail()).equals("manager")) {
                managersArray.add(mappedAssociate(objectMapper, user, start, end));
            }
        }
        ArrayNode employeesArray = objectMapper.createArrayNode();
        for (User user : associates) {
            if (roles.get(user.getEmail()).equals("employee")) {
                employeesArray.add(mappedAssociate(objectMapper, user, start, end));
            }
        }

        output.set("managers", managersArray);
        output.set("employees", employeesArray);
        output.put("total spent", totalSpentInInterval);
        output.put("total deposited", totalDepositedInInterval);
        return output;
    }

    private ObjectNode mappedAssociate(final ObjectMapper objectMapper, final User user,
                                       final int start, final int end) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("username", user.getLastName() + " " + user.getFirstName());
        double spent = spentInInterval(user.getEmail(), start, end);
        totalSpentInInterval += spent;
        userNode.put("spent", spent);
        double deposited = depositedInInterval(user.getEmail(), start, end);
        totalDepositedInInterval += deposited;
        userNode.put("deposited", deposited);
        return userNode;
    }

    /**
     * Generates a commerciant report for the business account.
     * @param objectMapper the object mapper used to create the JSON object
     * @param start the start timestamp of the report
     * @param end the end timestamp of the report
     * @return the JSON object representing the report
     */
    public ObjectNode mappedCommerciantReport(final ObjectMapper objectMapper,
                                              final int start, final int end) {
        ObjectNode output = objectMapper.createObjectNode();
        output.put("IBAN", iban);
        output.put("balance", balance);
        output.put("currency", currency);
        output.put("spending limit", spendingLimit);
        output.put("deposit limit", depositLimit);

        ArrayNode commerciantsArray = objectMapper.createArrayNode();
        output.set("commerciants", commerciantsArray);

        output.put("statistics type", "commerciant");
        return output;
    }

    private double spentInInterval(final String email, final int start, final int end) {
        return calculateAmount(email, start, end, spendingsHistory);
    }

    private double depositedInInterval(final String email, final int start, final int end) {
        return calculateAmount(email, start, end, depositsHistory);
    }

    /**
     * Calculates the amount spent or deposited in a given timestamp interval.
     * @param email the email of the associate
     * @param start the start timestamp of the interval
     * @param end the end timestamp of the interval
     * @param history the history of the associate's transactions
     * @return the amount spent or deposited in the interval
     */
    private double calculateAmount(final String email, final int start, final int end,
                                   final Map<Integer, Map<String, Double>> history) {

        double atStart = history.entrySet().stream()
                .filter(timestamp -> timestamp.getKey() <= start)
                .flatMap(timestamp -> timestamp.getValue().entrySet().stream())
                .filter(spent -> spent.getKey().equals(email))
                .mapToDouble(Map.Entry::getValue)
                .reduce((first, second) -> second).orElse(0.0);

        double atEnd = history.entrySet().stream()
                .filter(timestamp -> timestamp.getKey() >= start && timestamp.getKey() <= end)
                .flatMap(timestamp -> timestamp.getValue().entrySet().stream())
                .filter(spent -> spent.getKey().equals(email))
                .mapToDouble(Map.Entry::getValue)
                .reduce((first, second) -> second).orElse(0.0);

        return atEnd - atStart;
    }
}
