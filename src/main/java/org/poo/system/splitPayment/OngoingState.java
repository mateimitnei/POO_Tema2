package org.poo.system.splitPayment;

import org.poo.system.User;

public final class OngoingState extends State {

    public OngoingState(final Payment payment) {
        super(payment);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void process() {
        // Do nothing
    }

    @Override
    public void acceptedBy(final User user) {
        payment.getUsers().remove(user);

        if (payment.getUsers().isEmpty()) {
            payment.changeState(new FinalisedState(payment));
        }
    }
}
