package api;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UnsortedValidationResult that = (UnsortedValidationResult) o;
        return Objects.equals(violations, that.violations) && Objects.equals(warnings, that.warnings) && Objects
                .equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(violations, warnings, resources);
    }
}
