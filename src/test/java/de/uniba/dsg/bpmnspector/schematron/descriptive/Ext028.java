package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.028
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext028 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "A Sequence Flow must not cross the border of a Pool",
                "(//bpmn:sequenceFlow)[1]", 16);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("Success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "028";
    }
}
