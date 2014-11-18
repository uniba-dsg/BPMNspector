package de.uniba.dsg.ppn.ba.artifacts;

import de.uniba.dsg.bpmn.Violation;

public class Ext008 extends ArtifactTest {

    @Override
    protected void assertViolation(Violation v) {
        assertViolation(v, "//bpmn:messageFlow[@targetRef][0]", 7);
    }

    @Override
    protected String getErrorMessage() {
        return "An Artifact MUST NOT be a target for a Message Flow";
    }

    @Override
    protected String getExtNumber() {
        return "008";
    }

}
