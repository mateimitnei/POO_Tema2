package org.poo.system.accounts;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
import org.poo.system.Commerciant;
import org.poo.system.CommerciantList;
import org.poo.system.ExchangeCurrency;
import org.poo.system.User;
import org.poo.system.cards.Card;
import org.poo.system.transactions.Transaction;
import org.poo.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public abstract class BankAccount {
    private final String iban;
    private double balance;
    private double minBalance;
    private String currency;
    private ArrayList<Card> cards;
    private String alias;
    private User owner;
    private List<Transaction> transactionsLog;
    private Map<String, Boolean> discounts;
    private Map<Commerciant, Integer> transactionsCount;
    private Map<Commerciant, Double> totalSpent;
    private static final int THRESHOLD = 30;
    private static final double STANDARD_FEE = 0.002;
    private static final double SILVER_FEE = 0.001;
    private static final double SILVER_THRESHOLD = 500.0;
    private static final double PAYMENT_THRESHOLD = 300.0;

    public BankAccount(final String currency, final User owner) {
        this.iban = Utils.generateIBAN();
        this.currency = currency;
        this.owner = owner;
        balance = 0.0;
        minBalance = 0.0;
        alias = "";
        cards = new ArrayList<>();
        transactionsLog = new ArrayList<>();
        discounts = new HashMap<>();
        transactionsCount = new HashMap<>();
        totalSpent = new HashMap<>();
    }

    private void madeTransaction(final Commerciant commerciant, final double amount) {

        Map<String, String> commerciantsMap = CommerciantList.getInstance().getMap();
        if (commerciantsMap.get(commerciant.getName()).equals("NrOfTransactions")) {
            transactionsCount.put(commerciant, transactionsCount.getOrDefault(commerciant, 0) + 1);

            if (transactionsCount.get(commerciant) >= 10
                    && !discounts.containsKey("Tech")) {
                discounts.put("Tech", true);

            } else if (transactionsCount.get(commerciant) >= 5
                    && !discounts.containsKey("Clothes")) {
                discounts.put("Clothes", true);

            } else if (transactionsCount.get(commerciant) >= 2
                    && !discounts.containsKey("Food")) {
                discounts.put("Food", true);
            }

        } else { // Commerciant uses the SpendingThreshold strategy
            ExchangeCurrency exchanger = ExchangeCurrency.getInstance();
            Double converted = exchanger.exchange(currency, "RON", amount, new ArrayList<>());
            totalSpent.put(commerciant, totalSpent.getOrDefault(commerciant, 0.0) + converted);
        }
    }

    /**
     * Applies the cashback strategy of the commerciant to which a payment is being made.
     *
     * @param commerciant the commerciant
     * @param amount      the amount of the transaction
     */
    public void applyCashback(final Commerciant commerciant, final double amount) {
        double cashBackRate = commerciant.getCashbackStrategy()
                .calculateCashBack(this, commerciant, amount);

        if (cashBackRate > 0.0) {
            deposit(amount * cashBackRate);
            System.out.println("    Cashback: " + cashBackRate + " % of " + amount + " = " + amount * cashBackRate);
        }

        madeTransaction(commerciant, amount);
    }


    /**
     * Adds a transaction to be printed at "printTransactions" or "report".
     *
     * @param transaction the transaction to be added
     */
    public void addToTransactionLog(final Transaction transaction) {
        transactionsLog.add(transaction);
    }

    /**
     * Adds a card to the account.
     *
     * @param card the card to be added
     */
    public void addCard(final Card card) {
        cards.add(card);
    }

    /**
     * Deposits money into the account.
     *
     * @param amount the amount to be deposited
     */
    public void deposit(final double amount) {
        balance += amount;
    }

    /**
     * Withdraws money from the account.
     *
     * @param amount the amount to be withdrawn
     */
    public void withdraw(double amount, final boolean isPayement) {
        amount = applyFee(amount);
        if (balance >= amount) {
            balance -= amount;
        } else {
            throw new ArithmeticException("Insufficient funds");
        }

        // Check if the account is eligible for the silver to gold plan upgrade
        if (isPayement) {
            ExchangeCurrency exchanger = ExchangeCurrency.getInstance();
            double amountInLei = exchanger.exchange(currency, "RON", amount, new ArrayList<>());
            if (amountInLei >= PAYMENT_THRESHOLD && owner.getPlan().equals("silver")) {
                owner.setPaymentCounter(owner.getPaymentCounter() + 1);
                if (owner.getPaymentCounter() >= 5) {
                    owner.setPlan("gold");
                }
            }
        }
    }

    private double applyFee(double amount) {
        if (owner.getPlan().equals("standard")) {
            amount += STANDARD_FEE * amount;
            return amount;
        }
        ExchangeCurrency exchanger = ExchangeCurrency.getInstance();
        double amountInLei = exchanger.exchange(currency, "RON", amount, new ArrayList<>());
        if (owner.getPlan().equals("silver") && amountInLei >= SILVER_THRESHOLD) {
            amount += SILVER_FEE * amount;
        }
        return amount;
    }

    /**
     * Checks the status of the account and modifies the status of the card if necessary.
     *
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
     *
     * @return "classic" or "savings"
     */
    public abstract String getAccountType();

    /**
     * Maps the account to a JSON object.
     *
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
