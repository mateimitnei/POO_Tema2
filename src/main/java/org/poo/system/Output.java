package org.poo.system;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;

public final class Output {
    @Getter @Setter
    private ArrayNode output;
    private static Output instance;

    public static Output getInstance() {
        if (instance == null) {
            instance = new Output();
        }
        return instance;
    }
}
