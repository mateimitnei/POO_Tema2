package org.poo.system.accounts;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.system.ExchangeCurrency;
import org.poo.system.ExchangeRate;
import org.poo.system.User;
import org.poo.system.cards.Card;
import org.poo.system.transactions.Transaction;
import org.poo.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public abstract class BankAccount {
    private final String iban;
    private double balance;
    private double minBalance;
    private String currency;
    private ArrayList<Card> cards;
    private String alias;
    private static final int THRESHOLD = 30;
    private List<Transaction> transactions;
    private User owner;

    public BankAccount(final String currency, final User owner) {
        this.iban = Utils.generateIBAN();
        balance = 0.0;
        minBalance = 0.0;
        this.currency = currency;
        cards = new ArrayList<>();
        alias = "";
        transactions = new ArrayList<>();
        this.owner = owner;
    }

    public void addTransaction(final Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * Adds a card to the account.
     * @param card the card to be added
     */
    public void addCard(final Card card) {
        cards.add(card);
    }

    /**
     * Deposits money into the account.
     * @param amount the amount to be deposited
     */
    public void deposit(final double amount) {
        balance += amount;
    }

    /**
     * Withdraws money from the account.
     * @param amount the amount to be withdrawn
     */
    public void withdraw(double amount, final ExchangeCurrency exchange) {
        amount = applyFee(amount, exchange);
        if (balance >= amount) {
            balance -= amount;
        } else {
            throw new ArithmeticException("Insufficient funds");
        }
    }

    public double applyFee(double amount, final ExchangeCurrency exchange) {
        if (owner.getPlan().equals("standard")) {
            amount += 0.02 * amount;
            return amount;
        }
        double converted = exchange.exchange(currency, "RON", amount, new ArrayList<>());
        if (owner.getPlan().equals("silver") && converted > 500.0) {
            amount += 0.01 * amount;
        }
        return amount;
    }

    /**
     * Checks the status of the account and modifies the status of the card if necessary.
     * @param cardNumber the card number
     * @return "active", "warning" or "frozen"
     */
    public String checkStatus(final String cardNumber) {
        if (balance <= minBalance) {
            for (Card card : cards) {
                if (card.getCardNumber().equals(cardNumber)) {
                    card.setStatus("frozen");
                }
            }
            return "frozen";
        } else if (balance - minBalance <= THRESHOLD) {
            return "warning";
        }

        return "active";
    }

    /**
     * Returns the account type.
     * @return "classic" or "savings"
     */
    public abstract String getAccountType();

    /**
     * Maps the account to a JSON object.
     * @param objectMapper the object mapper
     * @return the JSON object
     */
    public ObjectNode mappedAccount(final ObjectMapper objectMapper) {
        ObjectNode accountNode = objectMapper.createObjectNode();
        accountNode.put("IBAN", iban);
        accountNode.put("balance", balance);
        accountNode.put("currency", currency);
        accountNode.put("type", getAccountType());

        ArrayNode cardsArray = objectMapper.createArrayNode();
        for (Card card : cards) {
            cardsArray.add(card.mappedCard(objectMapper));
        }

        accountNode.set("cards", cardsArray);
        return accountNode;
    }
}
