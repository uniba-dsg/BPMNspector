package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext031 extends TestCase {

    private final static String ERRORMESSAGE = "A message flow must connect ’InteractionNodes’ from different Pools";
    private final static String XPATHSTRING = "//bpmn:messageFlow[0]";

    @Test
    public void testConstraintCircleFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_circle.bpmn"), 1);
        assertFirstViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintFromPoolFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_message_flow_from_pool.bpmn"), 2);
        assertFirstViolation(result.getViolations().get(0));
        assertTargetViolation(result.getViolations().get(1));
    }

    @Test
    public void testConstraintToPoolFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_message_flow_to_pool.bpmn"), 2);
        assertFirstViolation(result.getViolations().get(0));
        assertSourceViolation(result.getViolations().get(1));
    }

    @Test
    public void testConstraintSamePoolFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_message_flow_in_same_pool.bpmn"), 3);
        assertFirstViolation(result.getViolations().get(0));
        assertSourceViolation(result.getViolations().get(1));
        assertTargetViolation(result.getViolations().get(2));
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("Success.bpmn"));
    }

    private void assertFirstViolation(Violation v) {
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(7, v.getLine());
    }

    private void assertSourceViolation(Violation v) {
        assertEquals("A Start Event MUST NOT be a source for a message flow",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[@sourceRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    private void assertTargetViolation(Violation v) {
        assertEquals("An End Event MUST NOT be a target for a message flow",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[@targetRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "031";
    }
}
