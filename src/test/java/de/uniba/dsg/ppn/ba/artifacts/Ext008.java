package de.uniba.dsg.ppn.ba.artifacts;

public class Ext008 extends ArtifactTest {

    @Override
    protected void assertViolation(de.uniba.dsg.bpmnspector.common.Violation v) {
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
