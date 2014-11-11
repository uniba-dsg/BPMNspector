package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext100 extends TestCase {

    @Test
    public void testConstraintEventFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_event.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "//bpmn:subProcess[@triggeredByEvent = 'false']/bpmn:startEvent[0]",
                10);
    }

    @Test
    public void testConstraintTransactionEventFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_event_transaction.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:transaction/bpmn:startEvent[0]", 10);
    }

    @Test
    public void testConstraintEventRefFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_event_ref.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "//bpmn:subProcess[@triggeredByEvent = 'false']/bpmn:startEvent[0]",
                11);
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Test
    public void testConstraintEventSubProcessSuccess()
            throws BpmnValidationException {
        verifyValidResult(createFile("success_event_sub.bpmn"));
    }

    private void assertViolation(Violation v, String xpath, int line) {
        assertEquals(
                "No EventDefinition is allowed for Start Events in Sub-Process definitions",
                v.getMessage());
        assertEquals(xpath, v.getxPath());
        assertEquals(line, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "100";
    }
}
