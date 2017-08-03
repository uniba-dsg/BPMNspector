package de.uniba.dsg.bpmnspector.autofix;

import api.Violation;

import java.util.ArrayList;
import java.util.List;

public class FixReport {

    private final List<Violation> fixedViolations;

    public FixReport() {
        this.fixedViolations = new ArrayList<>();
    }

    public boolean violationsHaveBeenFixed() {
        return !fixedViolations.isEmpty();
    }

    public void addFixedViolation(Violation violation) {
        this.fixedViolations.add(violation);
    }

    public List<Violation> getFixedViolations() {
        return fixedViolations;
    }

    public static FixReport createUnchangedFixReport() {
        return new FixReport();
    }

}
