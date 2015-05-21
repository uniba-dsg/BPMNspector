package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.119
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext119 extends TestCase {

    private static final String ERR_MSG_CATCH = "An intermediateCatchEvent must not be a source of a messageFlow.";
    private static final String ERR_MSG_THROW = "An intermediateThrowEvent must not be a target of a messageFlow.";


    @Test
    public void testConstraintFailCatchInOut() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT119_failure_catchInOut.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_CATCH,
                "//bpmn:intermediateCatchEvent[bpmn:messageEventDefinition][0]", 10);
    }

    @Test
    public void testConstraintFailCatchOutgoing() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT119_failure_catchOutgoing.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_CATCH,
                "//bpmn:intermediateCatchEvent[bpmn:messageEventDefinition][0]", 10);
    }

    @Test
    public void testConstraintFailThrowInOut() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT119_failure_ThrowInOut.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_THROW,
                "//bpmn:intermediateThrowEvent[bpmn:messageEventDefinition][0]", 7);
    }

    @Test
    public void testConstraintFailThrowIncoming() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT119_failure_throwIncoming.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_THROW,
                "//bpmn:intermediateThrowEvent[bpmn:messageEventDefinition][0]", 7);
    }

    @Test
    public void testConstraintSuccessCatchIncoming() throws ValidationException {
        verifyValidResult(createFile("EXT119_success_catchIncoming.bpmn"));
    }

    @Test
    public void testConstraintSuccessThrowOutgoing() throws ValidationException {
        verifyValidResult(createFile("EXT119_success_ThrowOutgoing.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "119";
    }
}
