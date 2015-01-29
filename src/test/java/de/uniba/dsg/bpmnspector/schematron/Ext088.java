package de.uniba.dsg.bpmnspector.schematron;

import api.ValidationResult;
import api.ValidationException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.088
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext088 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(createFile("fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "A DataOutput must be referenced by at least one OutputSet",
                "//bpmn:dataOutput[0]", 5);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "088";
    }
}
