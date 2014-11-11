package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext146 extends TestCase {

    private final static String ERRORMESSAGE = "Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events";
    private final static String XPATHSTRING = "//bpmn:endEvent[0]";

    @Test
    public void testConstraintLinkFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_link.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 7);
    }

    @Test
    public void testConstraintTimerFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_timer.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 7);
    }

    @Test
    public void testConstraintTimerRefFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_timer_ref.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 8);
    }

    @Test
    public void testConstraintMultipleFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_multiple.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 7);
    }

    @Test
    public void testConstraintConditionalFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_conditional.bpmn"), 2);
        Violation v = result.getViolations().get(0);
        assertTrue(v.getMessage().contains("cvc-complex-type.2.4.b"));
        assertTrue(v.getMessage().contains("conditionalEventDefinition"));
        assertEquals("XSD-Check", v.getConstraint());
        assertEquals(9, v.getLine());
        assertViolation(result.getViolations().get(1), 7);
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Test
    public void testConstraintMultipleSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success_multiple.bpmn"));
    }

    private void assertViolation(Violation v, int line) {
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(line, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "146";
    }

}
