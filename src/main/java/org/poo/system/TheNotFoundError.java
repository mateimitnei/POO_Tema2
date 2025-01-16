package org.poo.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

public final class TheNotFoundError {

    private TheNotFoundError() { }

    public static ObjectNode makeOutput(final CommandInput input, final ObjectMapper objectMapper,
                                 final String description) {

        ObjectNode commandOutput = objectMapper.createObjectNode();
        ObjectNode output = objectMapper.createObjectNode();

        output.put("timestamp", input.getTimestamp());
        output.put("description", description);

        commandOutput.put("command", input.getCommand());
        commandOutput.set("output", output);
        commandOutput.put("timestamp", input.getTimestamp());

        return commandOutput;
    }
}
