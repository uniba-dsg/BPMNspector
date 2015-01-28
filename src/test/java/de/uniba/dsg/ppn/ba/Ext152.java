package de.uniba.dsg.ppn.ba;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
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
    public void testConstraintFail1() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_1.bpmn"), 2);
        assertViolation(result.getViolations().get(0),
                "//bpmn:sequenceFlow[0]", 16);
        assertViolation(
                result.getViolations().get(1),
                "If a start event is used to initiate a process, all flow nodes must have an incoming sequence flow",
                "//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:startEvent][0]",
                7);
    }

    @Test
    public void testConstraintFail2() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_2.bpmn"), 2);
        assertViolation(result.getViolations().get(0),
                "//bpmn:sequenceFlow[1]", 17);
        assertViolation(
                result.getViolations().get(1),
                "If end events are used, all flow nodes must have an outgoing sequence flow",
                "//bpmn:subProcess[@isForCompensation = 'false' and @triggeredByEvent = 'false'] [parent::*/bpmn:endEvent][0]",
                7);
    }

    @Test
    public void testConstraintSuccess() throws ValidatorException {
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
