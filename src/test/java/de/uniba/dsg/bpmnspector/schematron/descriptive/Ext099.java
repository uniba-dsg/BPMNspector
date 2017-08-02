package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
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
        ValidationResult result = verifyInvalidResult(
                createFile("fail_event.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "(//bpmn:process[./@id = //bpmn:callActivity/@calledElement])[1]",
                6);
    }

    @Test
    public void testConstraintEventRefFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_eventref.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "(//bpmn:process[./@id = //bpmn:callActivity/@calledElement])[1]",
                7);
    }

    @Test
    public void testConstraintImportedProcessFail()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
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
