package org.poo.system;

import lombok.Getter;
import org.poo.fileio.CommerciantInput;
import org.poo.system.cashback.CashBackStrategy;
import org.poo.system.cashback.CashBackFactory;

@Getter
public class Commerciant {
    private final String name;
    private final int id;
    private final String account;
    private final String type;
    private final CashBackStrategy cashbackStrategy;

    public Commerciant(final CommerciantInput input) {
        this.name = input.getCommerciant();
        this.id = input.getId();
        this.account = input.getAccount();
        this.type = input.getType();
        cashbackStrategy = CashBackFactory.createCashBackStrategy(input.getCashbackStrategy());
    }
}
