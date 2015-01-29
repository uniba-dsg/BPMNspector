package de.uniba.dsg.bpmnspector.schematron;

import api.ValidationResult;
import api.ValidationException;
import api.Violation;
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
        ValidationResult result = verifyInValidResult(
                createFile("fail_cancel_end_event.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 9);
    }

    @Test
    public void testConstraintEventRefFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_sub_process.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 24);
    }

    @Test
    public void testConstraintCancelBoundaryEventFail()
            throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_cancel_boundary_event.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 18);
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
        assertViolation(v, "//bpmn:cancelEventDefinition[0]", line);
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
