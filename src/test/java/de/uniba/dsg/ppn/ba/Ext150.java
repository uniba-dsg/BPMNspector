package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext150 extends TestCase {

    private final static String ERRORMESSAGEONE = "If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow";
    private final static String ERRORMESSAGETWO = "If end events are used, all flow nodes must have an outgoing sequence flow";

    @Test
    public void testConstraintNormalSequenceFlowFail1()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_normal_sequence_flow_missing_1.bpmn"), 2);
        assertSecondViolation(
                result.getViolations().get(1),
                "//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:endEvent][0]",
                7);
        assertFirstViolation(
                result.getViolations().get(0),
                "//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:startEvent][4]",
                55);
    }

    @Test
    public void testConstraintNormalSequenceFlowFail2()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_normal_sequence_flow_missing_2.bpmn"), 2);
        assertSecondViolation(
                result.getViolations().get(1),
                "//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent][3]",
                49);
        assertFirstViolation(
                result.getViolations().get(0),
                "//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent][0]",
                8);
    }

    @Test
    public void testConstraintSequenceFlowInSubProcessFail1()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_sequence_flow_in_sub_process_missing_1.bpmn"),
                2);
        assertSecondViolation(
                result.getViolations().get(1),
                "//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent][0]",
                10);
        assertFirstViolation(result.getViolations().get(0),
                "//bpmn:parallelGateway[parent::*/bpmn:startEvent][0]", 13);
    }

    @Test
    public void testConstraintSequenceFlowInSubProcessFail2()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_sequence_flow_in_sub_process_missing_2.bpmn"),
                2);
        Violation v = result.getViolations().get(0);
        assertEquals(
                "A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows",
                v.getMessage());
        assertEquals("//bpmn:parallelGateway[0]", v.getxPath());
        assertEquals(14, v.getLine());
        assertFirstViolation(
                result.getViolations().get(1),
                "//bpmn:callActivity[@isForCompensation = 'false'] [parent::*/bpmn:startEvent][0]",
                38);
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Test
    public void testConstraintSuccess2() throws BpmnValidationException {
        verifyValidResult(createFile("success_2.bpmn"));
    }

    @Test
    public void testConstraintLinkEventSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success_linkevent.bpmn"));
    }

    private void assertFirstViolation(Violation v, String xpath, int line) {
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertEquals(xpath, v.getxPath());
        assertEquals(line, v.getLine());
    }

    private void assertSecondViolation(Violation v, String xpath, int line) {
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals(xpath, v.getxPath());
        assertEquals(line, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "150";
    }

}
