package de.uniba.dsg.bpmnspector.schematron.full;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.037
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext037 extends TestCase {

    private static final String ERR_MSG = "The value of the startQuantity attribute of an activity MUST NOT be less than 1.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT037_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:*[@startQuantity])[1]", 7);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT037_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "037";
    }
}
