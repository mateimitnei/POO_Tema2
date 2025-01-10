package org.poo.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;
import org.poo.system.accounts.BankAccount;
import org.poo.system.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private List<BankAccount> accounts;

    public User(final String firstName, final String lastName, final String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        accounts = new ArrayList<>();
    }

    public void addAccount(final BankAccount account) {
        accounts.add(account);
    }

    /**
     * Deletes an account if it has no money.
     * @param iban the account's IBAN
     * @return "deleted" if the account was deleted, "has money" if the account has money,
     * "error" if the account was not found
     */
    public String deleteAccount(final String iban) {
        for (BankAccount account : accounts) {
            if (account.getIban().equals(iban) && account.getBalance() == 0.0) {
                account.getCards().clear();
                accounts.remove(account);
                return "deleted";

            } else if (account.getIban().equals(iban) && account.getBalance() > 0.0) {
                return "has money";
            }
        }

        return "error";
    }

    /**
     * Maps the user to a JSON object.
     * @param objectMapper the object mapper
     * @return the JSON object
     */
    public ObjectNode mappedUser(final ObjectMapper objectMapper) {
        ObjectNode userNode = objectMapper.createObjectNode();
        userNode.put("firstName", firstName);
        userNode.put("lastName", lastName);
        userNode.put("email", email);

        ArrayNode accountsArray = objectMapper.createArrayNode();
        for (BankAccount account : accounts) {
            accountsArray.add(account.mappedAccount(objectMapper));
        }

        userNode.set("accounts", accountsArray);
        return userNode;
    }
}
