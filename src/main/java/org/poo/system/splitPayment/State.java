package org.poo.system.splitPayment;

import org.poo.system.User;

public abstract class State {
    protected final Payment payment;

    public State(final Payment payment) {
        this.payment = payment;
    }

    /**
     * @return true if the payment is done, false otherwise
     */
    abstract boolean isDone();

    /**
     * Process the payment
     */
    abstract void process();

    /**
     * Register a user that accepted the payment
     * @param user the user that accepted the payment
     */
    abstract void acceptedBy(User user);
}
