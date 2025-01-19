package org.poo.system.splitPayment;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class AllPayments {
    private final List<Payment> payments;
    private static AllPayments instance;

    private AllPayments() {
        this.payments = new ArrayList<>();
    }

    /**
     * Singleton pattern.
     * @return the instance of AllPayments
     */
    public static AllPayments getInstance() {
        if (instance == null) {
            instance = new AllPayments();
        }
        return instance;
    }

    /**
     * Resets the list of payments so that every test starts with an empty list.
     */
    public void init() {
        payments.clear();
    }

    /**
     * Adds a payment to the list of payments.
     * @param payment the payment to be added
     */
    public void addPayment(final Payment payment) {
        payments.add(payment);
    }

    /**
     * Deletes a payment from the list of payments if it's done.
     * @param payment the payment to be deleted
     */
    public void deletePayment(final Payment payment) {
        if (payment.isDone()) {
            payments.remove(payment);
        }
    }
}
