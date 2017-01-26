package api;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SimpleValidationResult implements ValidationResult {

    private final List<Violation> violations = new ArrayList<>();
    private final List<Warning> warnings = new ArrayList<>();
    private final List<Path> files = new ArrayList<>();
    private final List<Resource> resources = new ArrayList<>();

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
        resources.add(new Resource(s));
        Collections.sort(files);
        Collections.sort(resources);
    }

    @Override
    public void addResource(Resource resource) {
        resources.add(resource);
        Collections.sort(resources);
    }

    @Override
    public List<Violation> getViolations() {
        return new ArrayList<>(violations);
    }

    @Override
    public List<Warning> getWarnings() {
        return new ArrayList<>(warnings);
    }

    @Override
    public List<Resource> getResources() {
        return new ArrayList<>(resources);
    }

    @Override
    public List<Path> getFoundFiles() {
        return new ArrayList<>(files);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SimpleValidationResult that = (SimpleValidationResult) o;
        return Objects.equals(violations, that.violations) && Objects.equals(warnings, that.warnings) && Objects
                .equals(files, that.files) && Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(violations, warnings, files, resources);
    }
}
