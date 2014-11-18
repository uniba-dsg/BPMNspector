package de.uniba.dsg.ppn.ba.artifacts;


public class Ext009 extends ArtifactTest {

    @Override
    protected void assertViolation(de.uniba.dsg.bpmnspector.common.Violation v) {
        assertViolation(v, "//bpmn:messageFlow[@sourceRef][0]", 7);
    }

    @Override
    protected String getErrorMessage() {
        return "An Artifact MUST NOT be a source for a Message Flow";
    }

    @Override
    protected String getExtNumber() {
        return "009";
    }
}
