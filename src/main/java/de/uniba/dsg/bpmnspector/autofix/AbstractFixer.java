package de.uniba.dsg.bpmnspector.autofix;

import api.Violation;
import org.jdom2.Document;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractFixer {

    private final String constraintId;

    private final FixingStrategy supportedStrategy;

    public AbstractFixer(String constraintId, FixingStrategy supportedStrategy) {
        this.constraintId = constraintId;
        this.supportedStrategy = supportedStrategy;
    }

    public abstract FixReport fixIssues(Document docToFix, List<Violation> violationList);

    public String getConstraintId() {
        return constraintId;
    }

    public FixingStrategy getSupportedStrategy() {
        return supportedStrategy;
    }

    protected List<Violation> getSupportedViolations(Map<Violation, FixingStrategy> violationMap) {
        return filterViolationsByStrategy(violationMap, supportedStrategy);
    }

    protected List<Violation> filterViolationsByStrategy(Map<Violation, FixingStrategy> violationMap, FixingStrategy strategy) {
        return violationMap.entrySet().stream()
                .filter(entry -> entry.getValue()==strategy)
                .filter(entry -> entry.getKey().getConstraint().equals(constraintId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
