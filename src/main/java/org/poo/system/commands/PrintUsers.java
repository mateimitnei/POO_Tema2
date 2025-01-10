package org.poo.system.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;
import org.poo.system.Engine;
import org.poo.system.Output;
import org.poo.system.User;

public final class PrintUsers implements Strategy {

    @Override
    public void execute(final CommandInput input) {
        Engine engine = Engine.getInstance();
        ObjectNode commandOutput = engine.getObjectMapper().createObjectNode();

        ArrayNode usersArray = engine.getObjectMapper().createArrayNode();
        for (User user : engine.getUsers()) {
            usersArray.add(user.mappedUser(engine.getObjectMapper()));
        }

        commandOutput.put("command", input.getCommand());
        commandOutput.set("output", usersArray);
        commandOutput.put("timestamp", input.getTimestamp());

        Output.getInstance().getOutput().add(commandOutput);
    }
}
