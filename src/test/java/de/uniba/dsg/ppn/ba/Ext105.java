package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext105 extends TestCase {

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_end_without_sub-events.bpmn"), 1);
        assertViolation(result.getViolations().get(0), "//bpmn:startEvent[0]",
                4);
    }

    @Test
    public void testConstraintSubFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_with_sub-startevent.bpmn"), 1);
        assertViolation(result.getViolations().get(0), "//bpmn:startEvent[1]",
                10);
    }

    @Test
    public void testConstraintSubSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success_with_sub-events.bpmn"));
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success_without_sub-events.bpmn"));
    }

    private void assertViolation(Violation v, String xpath, int line) {
        assertEquals(
                "An end event must be present when a start event is used in the same process level",
                v.getMessage());
        assertEquals(xpath, v.getxPath());
        assertEquals(line, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "105";
    }
}
