package de.uniba.dsg.ppn.ba;

import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext106 extends TestCase {

    @Test
    public void testConstraintEventFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_cancel_end_event.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 9);
    }

    @Test
    public void testConstraintEventRefFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_sub_process.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 24);
    }

    @Test
    public void testConstraintCancelBoundaryEventFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_cancel_boundary_event.bpmn"), 1);
        assertViolation(result.getViolations().get(0), 18);
    }

    @Test
    public void testConstraintCancelEventSuccess()
            throws BpmnValidationException {
        verifyValidResult(createFile("success_cancel_event.bpmn"));

    }

    @Test
    public void testConstraintCancelBoundaryEventSuccess()
            throws BpmnValidationException {
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
