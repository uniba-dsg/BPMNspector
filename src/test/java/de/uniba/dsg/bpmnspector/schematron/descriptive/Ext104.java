package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.104
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext104 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "An End Event must not have an outgoing sequence flow",
                "(//bpmn:endEvent)[1]", 7);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "104";
    }
}
