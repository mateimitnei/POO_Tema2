package org.poo.system.splitPayment;

import org.poo.system.User;

public abstract class State {
    protected final Payment payment;

    public State(final Payment payment) {
        this.payment = payment;
    }

    abstract boolean isDone();
    abstract void process();
    abstract void acceptedBy(User user);
}
