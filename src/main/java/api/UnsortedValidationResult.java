package api;


import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UnsortedValidationResult implements ValidationResult {

    private final List<Violation> violations = new ArrayList<>();
    private final List<Warning> warnings = new ArrayList<>();
    private final List<Path> files = new ArrayList<>();

    @Override
    public void addWarning(Warning warning) {
        warnings.add(warning);
    }

    @Override
    public void addViolation(Violation violation) {
        violations.add(violation);
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

    @Override
    public List<Path> getFoundFiles() {
        return new ArrayList<>(files);
    }

}
