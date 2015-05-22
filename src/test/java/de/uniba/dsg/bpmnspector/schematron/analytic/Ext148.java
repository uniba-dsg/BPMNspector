package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.148
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext148 extends TestCase {

    private static final String ERR_MSG = "Only messageEventDefinitions, timerEventDefinitions, " +
            "conditionalEventDefinitions, linkEventDefinitions and signalEventDefinitions are allowed for " +
            "intermediate catch events.";


    @Test
    public void testConstraintFailCancelCatch() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT148_failure_cancelCatch.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateCatchEvent[0]", 7);
    }

    @Test
    public void testConstraintFailCompensateCatch() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT148_failure_compensateCatch.bpmn"), 2);
        assertViolation(result.getViolations().get(1), ERR_MSG,
                "//bpmn:intermediateCatchEvent[0]", 7);
    }

    @Test
    public void testConstraintFailErrorCatch() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT148_failure_errorCatch.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateCatchEvent[0]", 7);
    }

    @Test
    public void testConstraintFailEscalationCatch() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT148_failure_escalationCatch.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateCatchEvent[0]", 7);
    }

    @Test
    public void testConstraintFailNoneCatch() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT148_failure_noneCatch.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateCatchEvent[0]", 7);
    }

    @Test
    public void testConstraintFailTerminateCatch() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT148_failure_terminateCatch.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateCatchEvent[0]", 7);
    }

    @Override
    protected String getExtNumber() {
        return "148";
    }
}
