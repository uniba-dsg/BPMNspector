package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationResult;
import api.ValidationException;
import api.Violation;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.106
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext106 extends TestCase {

    @Test
    public void testConstraintEventFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_cancel_end_event.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 7);
    }

    @Test
    public void testConstraintEventRefFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_sub_process.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 22);
    }

    @Test
    public void testConstraintCancelEventSuccess()
            throws ValidationException {
        verifyValidResult(createFile("success_cancel_event.bpmn"));

    }

    @Test
    public void testConstraintCancelBoundaryEventSuccess()
            throws ValidationException {
        verifyValidResult(createFile("success_cancel_boundary_event.bpmn"));
    }

    private void assertViolation(Violation v, int line) {
        assertViolation(v, "//bpmn:endEvent[./bpmn:cancelEventDefinition][0]", line);
    }

    @Override
    protected String getErrorMessage() {
        return "A cancel EndEvent is only allowed in a transaction sub-process";
    }

    @Override
    protected String getExtNumber() {
        return "106";
    }

}
