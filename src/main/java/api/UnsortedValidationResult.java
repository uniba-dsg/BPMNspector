package api;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UnsortedValidationResult implements ValidationResult {

    private final List<Violation> violations = new ArrayList<>();
    private final List<Warning> warnings = new ArrayList<>();
    private final List<Resource> resources = new ArrayList<>();

    @Override public void addWarning(Warning warning) {
        warnings.add(warning);
    }

    @Override public void addViolation(Violation violation) {
        violations.add(violation);
    }

    @Override public void addFile(Path s) {
        resources.add(new Resource(s));
    }

    @Override public void addResource(Resource resource) {
        resources.add(resource);
    }

    @Override public List<Violation> getViolations() {
        return new ArrayList<>(violations);
    }

    @Override public List<Warning> getWarnings() {
        return new ArrayList<>(warnings);
    }

    @Override public List<Resource> getResources() {
        return new ArrayList<>(resources);
    }

}
