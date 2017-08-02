package de.uniba.dsg.bpmnspector.schematron.artifacts.sequenzflow;

import api.Violation;

/**
 * Test class for testing Constraint EXT.007
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext007 extends AbstractArtifactSequenceFlowTest {

    private final static String ERRORMESSAGEONE = "An Artifact MUST NOT be a source for a Sequence Flow";
    private final static String ERRORMESSAGETWO = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source";
    private final static String ERRORMESSAGETHREE = "The source element of the sequence flow must reference the SequenceFlow definition using their outgoing attribute.";
    private final static String XPATHSTRING = "(//bpmn:sequenceFlow[@sourceRef])[1]";

    @Override
    protected void assertFirstViolation(Violation v, String fileName) {
        assertViolation(v, ERRORMESSAGEONE, fileName, XPATHSTRING, 7);
    }

    @Override
    protected void assertSecondViolation(Violation v, String fileName, int line) {
        assertViolation(v, ERRORMESSAGETWO, fileName,
                "(//bpmn:*[./@id = //bpmn:sequenceFlow/@sourceRef and ancestor::bpmn:process])[1]", line);
    }

    @Override
    protected void assertThirdViolation(Violation v, String fileName) {
        assertViolation(v, ERRORMESSAGETHREE, fileName, XPATHSTRING, 7);
    }

    @Override
    protected String getExtNumber() {
        return "007";
    }
}
