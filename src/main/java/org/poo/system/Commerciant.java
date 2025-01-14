package org.poo.system;

import org.poo.fileio.CommerciantInput;

public class Commerciant {
    private final String commerciant;
    private final int id;
    private final String account;
    private final String type;
    private final String cashbackStrategy;

    public Commerciant(final CommerciantInput input) {
        this.commerciant = input.getCommerciant();
        this.id = input.getId();
        this.account = input.getAccount();
        this.type = input.getType();
        this.cashbackStrategy = input.getCashbackStrategy();
    }
}
