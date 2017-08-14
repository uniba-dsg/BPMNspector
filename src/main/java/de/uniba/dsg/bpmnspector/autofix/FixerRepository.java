package de.uniba.dsg.bpmnspector.autofix;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FixerRepository {

    private final Map<FixerIdentifier, ViolationFixer> availableFixes;

    public FixerRepository() {
        this.availableFixes = new HashMap<>();
        registerExistingFixers();
    }

    private void registerExistingFixers() {
        availableFixes.put(EXT128AutoFixer.getFixerIdentifier(), new EXT128AutoFixer());
        availableFixes.put(EXT012FirstOptionNotExecutableFixer.getFixerIdentifier(), new EXT012FirstOptionNotExecutableFixer());
        availableFixes.put(EXT012SecondOptionMarkFormalExpFixer.getFixerIdentifier(), new EXT012SecondOptionMarkFormalExpFixer());
        availableFixes.put(EXT105AutoFixer.getFixerIdentifier(), new EXT105AutoFixer());
        availableFixes.put(EXT098RemoveInvalidTypeFixer.getFixerIdentifier(), new EXT098RemoveInvalidTypeFixer());
    }

    public Optional<ViolationFixer> getFixerForConstraintAndStrategy(String constraintId, FixingStrategy strategy) {
        FixerIdentifier identifier = new FixerIdentifier(constraintId, strategy);
        return Optional.ofNullable(availableFixes.get(identifier));
    }
}
