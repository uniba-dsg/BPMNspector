package de.uniba.dsg.ppn.ba.artifacts;

import api.Violation;

/**
 * Test class for testing Constraint EXT.008
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext008 extends AbstractArtifactTest {

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
