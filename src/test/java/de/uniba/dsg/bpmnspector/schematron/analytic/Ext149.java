package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.149
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext149 extends TestCase {

    private static final String ERR_MSG = "Only messageEventDefinitions, escalationEventDefinitions, " +
            "compensationEventDefinitions, linkEventDefinitions and signalEventDefinitions are allowed for " +
            "intermediate throw events.";


    @Test
    public void testConstraintFailCancelThrow() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT149_failure_cancelThrow.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateThrowEvent[bpmn:timerEventDefinition or bpmn:errorEventDefinition or " +
                        "bpmn:cancelEventDefinition or bpmn:conditionalEventDefinition or bpmn:terminateEventDefinition][0]", 7);
    }

    @Test
    public void testConstraintFailConditionalThrow() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT149_failure_conditionalThrow.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateThrowEvent[bpmn:timerEventDefinition or bpmn:errorEventDefinition or " +
                        "bpmn:cancelEventDefinition or bpmn:conditionalEventDefinition or bpmn:terminateEventDefinition][0]", 7);
    }

    @Test
    public void testConstraintFailErrorThrow() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT149_failure_errorThrow.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateThrowEvent[bpmn:timerEventDefinition or bpmn:errorEventDefinition or " +
                        "bpmn:cancelEventDefinition or bpmn:conditionalEventDefinition or bpmn:terminateEventDefinition][0]", 7);
    }

    @Test
    public void testConstraintFailTerminateThrow() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT149_failure_terminateThrow.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateThrowEvent[bpmn:timerEventDefinition or bpmn:errorEventDefinition or " +
                        "bpmn:cancelEventDefinition or bpmn:conditionalEventDefinition or bpmn:terminateEventDefinition][0]", 7);
    }

    @Test
    public void testConstraintFailTimerThrow() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT149_failure_timerThrow.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateThrowEvent[bpmn:timerEventDefinition or bpmn:errorEventDefinition or " +
                        "bpmn:cancelEventDefinition or bpmn:conditionalEventDefinition or bpmn:terminateEventDefinition][0]", 7);
    }

    @Override
    protected String getExtNumber() {
        return "149";
    }
}
