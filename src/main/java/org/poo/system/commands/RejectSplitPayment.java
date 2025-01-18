package org.poo.system.commands;

import org.poo.fileio.CommandInput;
import org.poo.system.User;
import org.poo.system.splitPayment.AllPayments;
import org.poo.system.splitPayment.Payment;

public final class RejectSplitPayment implements Strategy {

    @Override
    public void execute(final CommandInput input) {
        AllPayments payments = AllPayments.getInstance();

        for (Payment payment : payments.getPayments()) {
            for (User user : payment.getUsers()) {
                if (user.getEmail().equals(input.getEmail())) {
                    payment.rejectedBy(user);
                    payment.process();
                    return;
                }
            }
        }
    }
}
