package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.105
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext105 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_end_without_sub-events.bpmn"), 1);
        assertViolation(result.getViolations().get(0), "(//bpmn:startEvent)[1]",
                4);
    }

    @Test
    public void testConstraintSubFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_with_sub-startevent.bpmn"), 1);
        assertViolation(result.getViolations().get(0), "(//bpmn:startEvent)[2]",
                10);
    }

    @Test
    public void testConstraintSubSuccess() throws ValidationException {
        verifyValidResult(createFile("success_with_sub-events.bpmn"));
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success_without_sub-events.bpmn"));
    }

    @Override
    protected String getErrorMessage() {
        return "An end event must be present when a start event is used in the same process level";
    }

    @Override
    protected String getExtNumber() {
        return "105";
    }
}
