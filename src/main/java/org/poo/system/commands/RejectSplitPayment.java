package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.TheNotFoundError;
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

        // If the user was not found
        ObjectNode commandOutput = TheNotFoundError
                .makeOutput(input, Engine.getInstance().getObjectMapper(), "User not found");

        Output.getInstance().getOutput().add(commandOutput);
    }
}
