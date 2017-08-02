package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.094
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext094 extends TestCase {

    private static final String ERR_MSG = "The itemDefinition for each eventDefinition must be equivalent to the " +
            "itemDefinition of the corresponding dataInput/dataOutput.";

    @Test
    public void testConstraintFailMultiple() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT094_failure_multiple.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "(//bpmn:signalEventDefinition[preceding-sibling::bpmn:dataInput])[1]", 36);
    }

    @Test
    public void testConstraintFailSingleErrorEvent() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT094_failure_singleErrorEvent.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "(//bpmn:errorEventDefinition[preceding-sibling::bpmn:dataInput])[1]", 24);
    }

    @Test
    public void testConstraintFailSingleEscalationEvent() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT094_failure_singleEscalationEvent.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "(//bpmn:escalationEventDefinition[preceding-sibling::bpmn:dataInput])[1]", 24);
    }

    @Test
    public void testConstraintFailSingleMessageEvent() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT094_failure_singleMessageEvent.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "(//bpmn:messageEventDefinition[preceding-sibling::bpmn:dataOutput])[1]", 22);
    }

    @Test
    public void testConstraintSuccessSingleErrorEvent() throws ValidationException {
        verifyValidResult(createFile("EXT094_success_singleErrorEvent.bpmn"));
    }

    @Test
    public void testConstraintSuccessSingleEscalationEvent() throws ValidationException {
        verifyValidResult(createFile("EXT094_success_singleEscalationEvent.bpmn"));
    }
    
    @Test
    public void testConstraintSuccessSingleSignalEvent() throws ValidationException {
        verifyValidResult(createFile("EXT094_success_singleSignalEvent.bpmn"));
    }

    @Test
    public void testConstraintSuccessSingleMessageEvent() throws ValidationException {
        verifyValidResult(createFile("EXT094_success_singleMessageEvent.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "094";
    }
}
