package api;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public interface ValidationResult {

    List<Violation> getViolations();

    List<Warning> getWarnings();

    List<Path> getFoundFiles();

    void addWarning(Warning warning);

    void addViolation(Violation violation);

    void addFile(Path s);

    default List<String> getViolatedConstraints() {
        return getViolations().stream().map(Violation::getConstraint).distinct().sorted().collect(Collectors.toList());
    }

    default List<Path> getFilesWithViolations() {
        return getViolations().stream().map(v -> v.getLocation().getFileName()).distinct().sorted().collect(Collectors.toList());
    }

    default boolean isValid() {
        return getViolations().isEmpty();
    }
}
