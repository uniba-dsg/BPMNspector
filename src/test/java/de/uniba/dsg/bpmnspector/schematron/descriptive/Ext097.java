package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationException;
import api.ValidationResult;
import api.Violation;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.097
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext097 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_end_without_sub-events.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 8);
    }

    @Test
    public void testConstraintSubFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_with_sub-endevent.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 10);
    }

    @Test
    public void testConstraintSubSuccess() throws ValidationException {
        verifyValidResult(createFile("success_with_sub-events.bpmn"));
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success_without_sub-events.bpmn"));

    }

    private void assertViolation(Violation v, int line) {
        assertViolation(v, "(//bpmn:endEvent)[1]", line);
    }

    @Override
    protected String getErrorMessage() {
        return "A Start event (or an instantiating ReceiveTask) must be present when an End event is used in the same process level";
    }

    @Override
    protected String getExtNumber() {
        return "097";
    }
}
