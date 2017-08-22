package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.150
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext150 extends TestCase {

    private final static String ERRORMESSAGEONE = "If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow";

    @Test
    public void testConstraintNormalSequenceFlowFail1()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_normal_sequence_flow_missing_1.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                ERRORMESSAGEONE,
                "(//bpmn:task[@isForCompensation = 'false'] [parent::*/bpmn:startEvent])[5]",
                68);
    }

    @Test
    public void testConstraintNormalSequenceFlowFail2()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_normal_sequence_flow_missing_2.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                ERRORMESSAGEONE,
                "(//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent])[1]",
                14);
    }

    @Test
    public void testConstraintSequenceFlowInSubProcessFail1()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_sequence_flow_in_sub_process_missing_1.bpmn"),
                1);
        assertViolation(
                result.getViolations().get(0),
                ERRORMESSAGEONE,
                "(//bpmn:serviceTask[@isForCompensation = 'false'] [parent::*/bpmn:startEvent])[1]",
                17);
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

    @Override
    protected String getExtNumber() {
        return "150";
    }

}
