package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.057
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext057 extends TestCase {

    private static final String ERR_MSG = "An Event Sub-Process MUST NOT have any incoming or outgoing Sequence Flows.";

    @Test
    public void testConstraintFailInvalidIncoming() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT057_fail_incoming_seqFlow.bpmn"),
                2);
        assertViolation(result.getViolations().get(1),
                ERR_MSG,
                "//bpmn:subProcess[@triggeredByEvent='true'][0]", 30);
    }

    @Test
    public void testConstraintFailInvalidOutgoing() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT057_fail_outgoing_seqFlow.bpmn"),
                2);
        assertViolation(result.getViolations().get(1),
                ERR_MSG,
                "//bpmn:subProcess[@triggeredByEvent='true'][0]", 30);
    }

    @Test
    public void testConstraintSuccessNoSeqFlow() throws ValidationException {
        verifyValidResult(createFile("EXT057_success_no_seqFlow.bpmn"));
    }

    @Test
    public void testConstraintSuccessNoEventSubProcess() throws ValidationException {
        verifyValidResult(createFile("EXT057_success_no_eventSubProcess.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "057";
    }
}
