package de.uniba.dsg.bpmnspector.autofix;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FixerRepository {

    private final Map<String, AbstractFixer> availableFixes;

    public FixerRepository() {
        this.availableFixes = new HashMap<>();
        registerExistingFixers();
    }

    private void registerExistingFixers() {
        availableFixes.put(EXT128AutoFixer.CONSTRAINT_ID_EXT128, new EXT128AutoFixer());
    }

    public Optional<AbstractFixer> getFixerForConstraintAndStrategy(String constraintId, FixingStrategy strategy) {
        return availableFixes.entrySet().stream()
                .filter(e -> e.getKey().equals(constraintId))
                .filter(e -> e.getValue().getSupportedStrategy() == strategy)
                .map(Map.Entry::getValue)
                .findFirst();
    }
}
