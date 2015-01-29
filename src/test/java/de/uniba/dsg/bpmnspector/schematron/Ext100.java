package de.uniba.dsg.bpmnspector.schematron;

import api.ValidationResult;
import api.ValidationException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.100
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext100 extends TestCase {

    @Test
    public void testConstraintEventFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_event.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "//bpmn:subProcess[@triggeredByEvent = 'false']/bpmn:startEvent[0]",
                10);
    }

    @Test
    public void testConstraintTransactionEventFail()
            throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_event_transaction.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:transaction/bpmn:startEvent[0]", 10);
    }

    @Test
    public void testConstraintEventRefFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_event_ref.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "//bpmn:subProcess[@triggeredByEvent = 'false']/bpmn:startEvent[0]",
                11);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Test
    public void testConstraintEventSubProcessSuccess()
            throws ValidationException {
        verifyValidResult(createFile("success_event_sub.bpmn"));
    }

    @Override
    protected String getErrorMessage() {
        return "No EventDefinition is allowed for Start Events in Sub-Process definitions";
    }

    @Override
    protected String getExtNumber() {
        return "100";
    }
}
