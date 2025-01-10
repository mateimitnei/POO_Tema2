package org.poo.system.commands;

import org.poo.fileio.CommandInput;

public interface Strategy {
    void execute(CommandInput input);
}
