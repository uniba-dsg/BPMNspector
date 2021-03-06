package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.152
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext152 extends TestCase {

    @Test
    public void testConstraintFail1() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_1.bpmn"), 2);
        assertViolation(result.getViolations().get(0),
                "(//bpmn:sequenceFlow)[1]", 16);
        assertViolation(
                result.getViolations().get(1),
                "If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow",
                "(//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent])[1]",
                7);
    }

    @Test
    public void testConstraintFail2() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_2.bpmn"), 2);
        assertViolation(result.getViolations().get(0),
                "(//bpmn:sequenceFlow)[2]", 17);
        assertViolation(
                result.getViolations().get(1),
                "If end events are used, all flow nodes must have an outgoing sequence flow",
                "(//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:endEvent])[1]",
                7);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Override
    protected String getErrorMessage() {
        return "A Sequence Flow must not cross the border of a Pool";
    }

    @Override
    protected String getExtNumber() {
        return "152";
    }
}
