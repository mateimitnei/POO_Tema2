package org.poo.system;

import lombok.Getter;
import org.poo.fileio.CommerciantInput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public final class CommerciantList {
    private Map<String, String> map;
    private List<Commerciant> commerciants;
    private static CommerciantList instance;

    private CommerciantList() { }

    /**
     * Singleton instance for CommerciantList.
     *
     * @return the newly created or already existent instance
     */
    public static CommerciantList getInstance() {
        if (instance == null) {
            instance = new CommerciantList();
        }
        return instance;
    }

    /**
     * Initializes the commerciants with the inputCommerciants
     * and their respective cashback strategies.
     *
     * @param inputCommerciants the commerciants to be initialized
     */
    public void init(CommerciantInput[] inputCommerciants) {
        map = new HashMap<>();
        commerciants = new ArrayList<>();

        for (CommerciantInput commerciantInput : inputCommerciants) {
            Commerciant commerciant = new Commerciant(commerciantInput);
            commerciants.add(commerciant);
            map.put(commerciant.getName(), commerciantInput.getCashbackStrategy());
        }
    }
}
