package de.uniba.dsg.ppn.ba;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.107
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext107 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(createFile("fail.bpmn"),
                2);
        assertViolation(
                result.getViolations().get(0),
                "The target element of the sequence flow must reference the SequenceFlow definition using their incoming attribute.",
                "//bpmn:sequenceFlow[@targetRef][0]", 9);
        assertViolation(result.getViolations().get(1),
                "An End Event MUST have at least one incoming Sequence Flow",
                "//bpmn:endEvent[0]", 4);
    }

    @Test
    public void testConstraintSuccess() throws ValidatorException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "107";
    }
}
