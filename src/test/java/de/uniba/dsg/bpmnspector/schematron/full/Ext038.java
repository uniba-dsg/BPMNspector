package de.uniba.dsg.bpmnspector.schematron.full;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.038
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext038 extends TestCase {

    private static final String ERR_MSG = "The value of the completionQuantity attribute of an activity MUST NOT be less than 1.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT038_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:*[@completionQuantity][0]", 7);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT038_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "038";
    }
}
