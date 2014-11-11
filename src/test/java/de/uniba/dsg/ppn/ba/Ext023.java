package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext023 extends TestCase {

    @Test
    public void testConstraintNoIncomingFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_no_incoming.bpmn"), 1);
        Violation v = result.getViolations().get(0);
        assertEquals(
                "The target element of the sequence flow must reference the SequenceFlow definition using their incoming attribute.",
                v.getMessage());
        assertEquals("//bpmn:sequenceFlow[@targetRef][0]", v.getxPath());
        assertEquals(10, v.getLine());
    }

    @Test
    public void testConstraintNoOutgoingFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_no_outgoing.bpmn"), 1);
        Violation v = result.getViolations().get(0);
        assertEquals(
                "The source element of the sequence flow must reference the SequenceFlow definition using their outgoing attribute.",
                v.getMessage());
        assertEquals("//bpmn:sequenceFlow[@sourceRef][0]", v.getxPath());
        assertEquals(10, v.getLine());
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
