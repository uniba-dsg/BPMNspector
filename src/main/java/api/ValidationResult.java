package api;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public interface ValidationResult {

    List<Violation> getViolations();

    List<Warning> getWarnings();

    List<Resource> getResources();

    void addWarning(Warning warning);

    void addViolation(Violation violation);

    void addFile(Path s);

    void addResource(Resource resource);

    default List<Path> getFoundFiles() {
        return getResources().stream().filter(r -> (r.getType() == Resource.ResourceType.FILE) && r.getPath().isPresent())
                .map(r -> r.getPath().get()).collect(Collectors.toList());
    }

    default List<String> getViolatedConstraints() {
        return getViolations().stream().map(Violation::getConstraint).distinct().sorted().collect(Collectors.toList());
    }

    default List<Path> getFilesWithViolations() {
        return getViolations().stream().map(v -> v.getLocation().getResource())
                .filter(r -> (r.getType() == Resource.ResourceType.FILE && r.getPath().isPresent()))
                .map(r -> r.getPath().get()).distinct().sorted().collect(Collectors.toList());
    }

    default List<Resource> getResourcesWithViolations() {
        return getViolations().stream().map(v -> v.getLocation().getResource()).distinct().sorted().collect(
                Collectors.toList());
    }

    default boolean isValid() {
        return getViolations().isEmpty();
    }
}
