package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.062
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext062 extends TestCase {

    private static final String ERR_MSG = "Start Event, End Event, Conversations, Conversation Links and Choreography " +
            "Activities MUST NOT be used in an AdHocSubProcess.";


    @Test
    public void testConstraintFailStartEvent() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT062_failure_StartEvent.bpmn"), 2);
        assertViolation(result.getViolations().get(1), ERR_MSG,
                "(//bpmn:adHocSubProcess)[1]", 7);
    }

    @Test
    public void testConstraintFailEndEvent() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT062_failure_StartEvent.bpmn"), 2);
        assertViolation(result.getViolations().get(1), ERR_MSG,
                "(//bpmn:adHocSubProcess)[1]", 7);
    }

    @Test
    public void testConstraintFailChorActivity() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT062_failure_StartEvent.bpmn"), 2);
        assertViolation(result.getViolations().get(1), ERR_MSG,
                "(//bpmn:adHocSubProcess)[1]", 7);
    }
    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT062_success_DataSeqFlow.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "062";
    }
}
