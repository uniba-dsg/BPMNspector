package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext099 extends TestCase {

    @Test
    public void testConstraintEventFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_event.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "//bpmn:process[./@id = //bpmn:callActivity/@calledElement][0]",
                6);
    }

    @Test
    public void testConstraintEventRefFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_eventref.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "//bpmn:process[./@id = //bpmn:callActivity/@calledElement][0]",
                7);
    }

    @Test
    public void testConstraintImportedProcessFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_call_ref_process.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:*[@id = 'PROCESS_1'][0]", 3);
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Test
    public void testConstraintGlobalSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success_global.bpmn"));
    }

    private void assertViolation(Violation v, String xpath, int line) {
        assertEquals(
                "Referenced process must have at least one None Start Event",
                v.getMessage());
        assertEquals(xpath, v.getxPath());
        assertEquals(line, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "099";
    }
}
