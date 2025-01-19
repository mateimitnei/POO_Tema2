package org.poo.system.cards;

public final class CardFactory {

    private CardFactory() { }

    /**
     * Creates a new card based on the input.
     *
     * @param type the card type
     * @param iban the IBAN of the account
     * @return the created card
     */
    public static Card createCard(final String type, final String iban, final String cardNumber,
                                  final String email) {
        return switch (type) {
            case "createCard" -> new StandardCard(iban, cardNumber, email);
            case "createOneTimeCard" -> new OneTimePayCard(iban, cardNumber, email);
            default -> throw new IllegalArgumentException("Invalid card type.");
        };
    }
}
