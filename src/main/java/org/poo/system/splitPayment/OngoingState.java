package org.poo.system.splitPayment;

import org.poo.system.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OngoingState extends State {

    public OngoingState(Payment payment) {
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
    public void acceptedBy(User user) {
        payment.getUsers().remove(user);

        if (payment.getUsers().isEmpty()) {
            payment.changeState(new FinalisedState(payment));
        }
    }
}
