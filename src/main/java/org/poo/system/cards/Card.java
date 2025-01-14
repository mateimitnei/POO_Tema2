package org.poo.system.cards;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.poo.system.accounts.BankAccount;

@Getter
public abstract class Card {
    @Setter
    private String cardNumber;
    @Setter
    private String status;
    private final String asociatedIban;

    public Card(final String iban, final String cardNumber) {
        this.cardNumber = cardNumber;
        this.status = "active";
        asociatedIban = iban;
    }

    /**
     * Makes a payment with the card.
     * If it's a one-time pay card,it generates a new card number
     * (indirectly making it a new card).
     */
    public abstract void madePayment(BankAccount account, String email, int timestamp);

    /**
     * Maps the card to a JSON object.
     * @param objectMapper the object mapper
     * @return the JSON object
     */
    public ObjectNode mappedCard(final ObjectMapper objectMapper) {
        ObjectNode cardNode = objectMapper.createObjectNode();
        cardNode.put("cardNumber", cardNumber);
        cardNode.put("status", status);
        return cardNode;
    }
}
