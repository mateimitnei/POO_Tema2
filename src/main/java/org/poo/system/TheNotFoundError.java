package org.poo.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.fileio.CommandInput;

public final class TheNotFoundError {

    private TheNotFoundError() { }

    /**
     * Creates a JSON object for a "description" output.
     *
     * @param input the input of the command
     * @param objectMapper the object mapper
     * @param description the description of the output
     * @return the JSON object with the output of the command
     */
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
