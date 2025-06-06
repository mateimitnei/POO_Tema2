package org.poo.system.cards;

import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.CardCreated;
import org.poo.system.transactions.CardDeleted;
import org.poo.utils.Utils;

public final class OneTimePayCard extends Card {

    public OneTimePayCard(final String iban, final String cardNumber, final String email) {
        super(iban, cardNumber, email);
    }

    /**
     * Generates a new card number and sets the status to "active",
     * simulating the deletion of the current card and the creation of a new one.
     */
    @Override
    public void madePayment(final BankAccount account, final String email, final int timestamp) {
        account.addToTransactionLog(new CardDeleted(timestamp, getCardNumber(), email,
                getAsociatedIban()));

        setCardNumber(Utils.generateCardNumber());
        setStatus("active");

        account.addToTransactionLog(new CardCreated(timestamp, getCardNumber(), email,
                getAsociatedIban()));
    }
}
