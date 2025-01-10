package org.poo.system.commands;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

public final class CommandHandler {
    @Getter @Setter
    private Strategy strategy;

    /**
     * Calls the execute method for strategy.
     * @param input the command input
     */
    public void applyStrategy(final CommandInput input) {
        strategy.execute(input);
    }
}
