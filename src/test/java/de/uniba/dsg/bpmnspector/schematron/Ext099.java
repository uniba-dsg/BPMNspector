package de.uniba.dsg.bpmnspector.schematron;

import api.ValidationResult;
import api.ValidationException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.099
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext099 extends TestCase {

    @Test
    public void testConstraintEventFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_event.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "//bpmn:process[./@id = //bpmn:callActivity/@calledElement][0]",
                6);
    }

    @Test
    public void testConstraintEventRefFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_eventref.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "//bpmn:process[./@id = //bpmn:callActivity/@calledElement][0]",
                7);
    }

    @Test
    public void testConstraintImportedProcessFail()
            throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_call_ref_process.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:*[@id = 'PROCESS_1']", 3);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Test
    public void testConstraintGlobalSuccess() throws ValidationException {
        verifyValidResult(createFile("success_global.bpmn"));
    }

    @Override
    protected String getErrorMessage() {
        return "Referenced process must have at least one None Start Event";
    }

    @Override
    protected String getExtNumber() {
        return "099";
    }
}
