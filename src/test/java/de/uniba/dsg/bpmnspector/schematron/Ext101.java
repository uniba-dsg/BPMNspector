package de.uniba.dsg.bpmnspector.schematron;

import api.ValidationResult;
import api.ValidationException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.101
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext101 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(createFile("fail.bpmn"),
                2);
        assertViolation(
                result.getViolations().get(0),
                "The source element of the sequence flow must reference the SequenceFlow definition using their outgoing attribute.",
                "//bpmn:sequenceFlow[@sourceRef][0]", 9);
        assertViolation(result.getViolations().get(1),
                "A startEvent must have a outgoing subelement",
                "//bpmn:startEvent[0]", 4);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "101";
    }
}
