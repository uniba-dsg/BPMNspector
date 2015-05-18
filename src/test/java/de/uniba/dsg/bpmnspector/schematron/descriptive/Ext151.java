package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationResult;
import api.ValidationException;
import api.Violation;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.151
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext151 extends TestCase {

    private final static String ERRORMESSAGEONE = "If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow";
    private final static String ERRORMESSAGETWO = "If end events are used, all flow nodes must have an outgoing sequence flow";

    @Test
    public void testConstraintNormalSequenceFlowFail1()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_normal_sequence_flow_missing_1.bpmn"), 2);
        assertFirstViolation(
                result.getViolations().get(0),
                "//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:startEvent][4]",
                54);
        assertSecondViolation(
                result.getViolations().get(1),
                "//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:endEvent][0]",
                7);
    }

    @Test
    public void testConstraintNormalSequenceFlowFail2()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_normal_sequence_flow_missing_2.bpmn"), 2);
        assertFirstViolation(
                result.getViolations().get(0),
                "//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent][0]",
                7);
        assertSecondViolation(
                result.getViolations().get(1),
                "//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent][3]",
                48);
    }

    @Test
    public void testConstraintSequenceFlowInSubProcessFail1()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_sequence_flow_in_sub_process_missing_1.bpmn"),
                2);
        assertFirstViolation(result.getViolations().get(0),
                "//bpmn:parallelGateway[parent::*/bpmn:startEvent][0]", 13);
        assertSecondViolation(
                result.getViolations().get(1),
                "//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:endEvent][0]",
                10);
    }

    @Test
    public void testConstraintSequenceFlowInSubProcessFail2()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_sequence_flow_in_sub_process_missing_2.bpmn"),
                4);
        assertViolation(
                result.getViolations().get(0),
                "A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows",
                "//bpmn:parallelGateway[0]", 14);
        assertFirstViolation(
                result.getViolations().get(1),
                "//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:startEvent][1]",
                17);
        assertFirstViolation(
                result.getViolations().get(2),
                "//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:startEvent][2]",
                20);
        assertSecondViolation(result.getViolations().get(3),
                "//bpmn:parallelGateway[parent::*/bpmn:endEvent][0]", 14);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Test
    public void testConstraintSuccess2() throws ValidationException {
        verifyValidResult(createFile("success_2.bpmn"));
    }

    @Test
    public void testConstraintLinkEventSuccess() throws ValidationException {
        verifyValidResult(createFile("success_linkevent.bpmn"));
    }

    private void assertFirstViolation(Violation v, String xpath, int line) {
        assertViolation(v, ERRORMESSAGEONE, xpath, line);
    }

    private void assertSecondViolation(Violation v, String xpath, int line) {
        assertViolation(v, ERRORMESSAGETWO, xpath, line);
    }

    @Override
    protected String getExtNumber() {
        return "151";
    }
}
