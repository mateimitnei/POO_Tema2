package org.poo.system.commands;

import org.poo.fileio.CommandInput;

public interface Strategy {

    /**
     * Executes the command.
     * @param input the input of the command
     */
    void execute(CommandInput input);
}
