package api;


import java.nio.file.Path;
import java.util.*;

public class SimpleValidationResult implements ValidationResult {

    private final List<Violation> violations = new ArrayList<>();
    private final List<Warning> warnings = new ArrayList<>();
    private final List<Path> files = new ArrayList<>();

    @Override
    public void addWarning(Warning warning) {
        warnings.add(warning);
        Collections.sort(warnings);
    }

    @Override
    public void addViolation(Violation violation) {
        violations.add(violation);
        Collections.sort(violations);
    }

    @Override
    public void addFile(Path s) {
        files.add(s);
    }

    @Override
    public List<Violation> getViolations() {
        return new ArrayList<>(violations);
    }

    @Override
    public List<Warning> getWarnings() {
        return new ArrayList<>(warnings);
    }

    public Set<Integer> getViolatedRules() {
        if (isValid()) {
            return Collections.emptySet();
        } else {
            Set<Integer> actualViolatedRules = new HashSet<>();
            for (Violation violation : getViolations()) {
                String withoutSA = violation.getConstraint().substring(2);
                actualViolatedRules.add(Integer.parseInt(withoutSA));
            }
            return actualViolatedRules;
        }
    }

    @Override
    public List<Path> getFoundFiles() {
        Collections.sort(files);
        return new ArrayList<>(files);
    }

    @Override
    public String toString() {
        return "Violated Rules " + getViolatedRules();
    }
}
