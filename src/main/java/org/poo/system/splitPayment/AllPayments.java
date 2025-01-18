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

    public static AllPayments getInstance() {
        if (instance == null) {
            instance = new AllPayments();
        }
        return instance;
    }

    public void reset() {
        payments.clear();
    }

    public void addPayment(final Payment payment) {
        payments.add(payment);
    }

    public void deletePayment(final Payment payment) {
        if (payment.isDone()) {
            payments.remove(payment);
        }
    }
}
