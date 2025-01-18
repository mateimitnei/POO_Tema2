package org.poo.system.splitPayment;

import lombok.Getter;
import org.poo.fileio.CommandInput;
import org.poo.system.User;
import org.poo.system.accounts.BankAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class Payment {
    private State state;
    private final CommandInput input;
    private final List<BankAccount> accounts;
    private final Map<BankAccount, Double> amounts;
    private final String currency;
    private final List<User> users;
    private final List<User> acceptedBy;

    public Payment(final List<BankAccount> accounts, final List<Double> amounts,
                   final CommandInput input) {

        this.input = input;
        this.currency = input.getCurrency();
        this.accounts = new ArrayList<>(accounts);
        this.amounts = new HashMap<>();
        this.users = new ArrayList<>();
        this.acceptedBy = new ArrayList<>();

        for (int i = 0; i < accounts.size(); i++) {
            this.amounts.put(accounts.get(i), amounts.get(i));
            if (!this.users.contains(accounts.get(i).getOwner())) {
                this.users.add(accounts.get(i).getOwner());
            }
        }

        state = new OngoingState(this);
    }

    public void changeState(State state) {
        this.state = state;
    }

    public boolean isDone() {
        return state.isDone();
    }

    public void process() {
        state.process();
    }

    public void acceptedBy(User user) {
        state.acceptedBy(user);
    }

    public void rejectedBy(User user) {
        state = new RejectedState(this);
    }
}
