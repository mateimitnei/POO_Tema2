package org.poo.system.splitPayment;

import org.poo.system.ExchangeCurrency;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.TransactionFactory;

import java.util.ArrayList;
import java.util.Map;

public final class FinalisedState extends State {

    public FinalisedState(final Payment payment) {
        super(payment);
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void process() {
        ExchangeCurrency exchangeRates = ExchangeCurrency.getInstance();

        String errorAccount = "";
        for (BankAccount account : payment.getAccounts()) {

            double convertedAmount = exchangeRates.exchange(payment.getCurrency(),
                    account.getCurrency(), payment.getAmounts().get(account), new ArrayList<>());

            // double amountWithFee = account.applyFee(convertedAmount);
            if (account.getBalance() < convertedAmount) {
                errorAccount = account.getIban();
                break;
            }
        }

        for (BankAccount account : payment.getAccounts()) {

            if (!errorAccount.isEmpty()) {
                account.addToTransactionLog(TransactionFactory.createTransaction(payment.getInput(),
                        Map.of("errorIBAN", errorAccount, "rejected", "")));

            } else {
                double convertedAmount = exchangeRates.exchange(payment.getCurrency(),
                        account.getCurrency(), payment.getAmounts().get(account),
                        new ArrayList<>());

                // account.withdraw(convertedAmount, true);
                account.setBalance(account.getBalance() - convertedAmount);
                account.checkGoldCompatibility(convertedAmount);

                account.addToTransactionLog(TransactionFactory.createTransaction(payment.getInput(),
                        Map.of("errorIBAN", "", "rejected", "")));
            }

        }

        AllPayments payments = AllPayments.getInstance();
        payments.deletePayment(payment);
    }

    @Override
    public void acceptedBy(final User user) {
        // Do nothing
    }
}
