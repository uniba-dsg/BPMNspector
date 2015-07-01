package de.uniba.dsg.bpmnspector.schematron.full;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.030
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext030 extends TestCase {

    private static final String ERR_MSG = "The value of maximum MUST be one or greater, AND MUST be equal or greater than the minimum value.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT030_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:participantMultiplicity[1]", 8);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT030_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "030";
    }
}
