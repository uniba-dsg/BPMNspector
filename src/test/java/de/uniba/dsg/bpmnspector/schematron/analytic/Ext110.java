package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.110
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext110 extends TestCase {

    private static final String ERR_MSG_CANCEL_TRUE = "A boundaryEvent with cancelActivity='true' must have at least " +
            "one eventDefinition but does not allow link or terminate eventDefinitions.";

    private static final String ERR_MSG_CANCEL_FALSE = "A boundaryEvent with cancelActivity='false' must have at least " +
            "one eventDefinition but does not allow link, error, cancel, compensate or terminate eventDefinitions.";



    @Test
    public void testConstraintFailCancelNone() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT110_failure_cancel_none.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_CANCEL_TRUE,
                "//bpmn:boundaryEvent[@cancelActivity='true'][0]", 16);
    }

    @Test
    public void testConstraintFailCancelTerminate() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT110_failure_cancel_terminate.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_CANCEL_TRUE,
                "//bpmn:boundaryEvent[@cancelActivity='true'][0]", 16);
    }

    @Test
    public void testConstraintFailCancelLink() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT110_failure_cancel_link.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_CANCEL_TRUE,
                "//bpmn:boundaryEvent[@cancelActivity='true'][0]", 16);
    }

    @Test
    public void testConstraintFailNoCancelNone() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT110_failure_noCancel_none.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_CANCEL_FALSE,
                "//bpmn:boundaryEvent[@cancelActivity='false'][0]", 16);
    }

    @Test
    public void testConstraintFailNoCancelTerminate() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT110_failure_noCancel_terminate.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_CANCEL_FALSE,
                "//bpmn:boundaryEvent[@cancelActivity='false'][0]", 16);
    }

    @Test
    public void testConstraintFailNoCancelCancel() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT110_failure_noCancel_cancel.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_CANCEL_FALSE,
                "//bpmn:boundaryEvent[@cancelActivity='false'][0]", 16);
    }

    @Test
    public void testConstraintFailNoCancelCompensate() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT110_failure_noCancel_compensate.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_CANCEL_FALSE,
                "//bpmn:boundaryEvent[@cancelActivity='false'][0]", 16);
    }

    @Test
    public void testConstraintFailNoCancelError() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT110_failure_noCancel_error.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG_CANCEL_FALSE,
                "//bpmn:boundaryEvent[@cancelActivity='false'][0]", 16);
    }


    @Test
    public void testConstraintSuccessCancelMessage() throws ValidationException {
        verifyValidResult(createFile("EXT110_success_cancel_message.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "110";
    }
}
