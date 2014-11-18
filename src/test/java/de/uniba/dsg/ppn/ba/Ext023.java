package de.uniba.dsg.ppn.ba;

import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext023 extends TestCase {

    @Test
    public void testConstraintNoIncomingFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_no_incoming.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "The target element of the sequence flow must reference the SequenceFlow definition using their incoming attribute.",
                "//bpmn:sequenceFlow[@targetRef][0]", 10);

    }

    @Test
    public void testConstraintNoOutgoingFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_no_outgoing.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "The source element of the sequence flow must reference the SequenceFlow definition using their outgoing attribute.",
                "//bpmn:sequenceFlow[@sourceRef][0]", 10);
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "023";
    }
}
