package de.uniba.dsg.bpmnspector.autofix;

import java.util.Objects;

public class FixerIdentifier {
    public final String constraintId;
    public final FixingStrategy fixingStrategy;

    public FixerIdentifier(String constraintId, FixingStrategy fixingStrategy) {
        this.constraintId = constraintId;
        this.fixingStrategy = fixingStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FixerIdentifier that = (FixerIdentifier) o;
        return Objects.equals(constraintId, that.constraintId) &&
                fixingStrategy == that.fixingStrategy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(constraintId, fixingStrategy);
    }
}
