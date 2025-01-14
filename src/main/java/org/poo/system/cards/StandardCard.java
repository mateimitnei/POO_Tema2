package org.poo.system.cards;

import org.poo.system.accounts.BankAccount;

public final class StandardCard extends Card {

    public StandardCard(final String iban, final String cardNumber) {
        super(iban, cardNumber);
    }

    /**
     * Does nothing because the standard card does not have any special behavior.
     */
    public void madePayment(final BankAccount account, final String email, final int timestamp) {
    }
}
