package api;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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


}
