package de.uniba.dsg.ppn.ba;

import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

/**
 * Test class for testing Constraint EXT.031
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
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
        assertViolation(v, ERRORMESSAGE, XPATHSTRING, 7);
    }

    private void assertSourceViolation(Violation v) {
        assertViolation(v,
                "A Start Event MUST NOT be a source for a message flow",
                "//bpmn:messageFlow[@sourceRef][0]", 7);
    }

    private void assertTargetViolation(Violation v) {
        assertViolation(v,
                "An End Event MUST NOT be a target for a message flow",
                "//bpmn:messageFlow[@targetRef][0]", 7);
    }

    @Override
    protected String getExtNumber() {
        return "031";
    }
}
