package org.poo.system.splitPayment;

import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.TransactionFactory;

import java.util.Map;

public final class RejectedState extends State {

    public RejectedState(final Payment payment) {
        super(payment);
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void process() {
        for (BankAccount account : payment.getAccounts()) {
            account.addToTransactionLog(TransactionFactory.createTransaction(payment.getInput(),
                    Map.of("errorIBAN", "", "rejected", "true")));
        }

        AllPayments payments = AllPayments.getInstance();
        payments.deletePayment(payment);
    }

    @Override
    public void acceptedBy(final User user) {
        // Do nothing
    }
}
