package de.uniba.dsg.bpmnspector.schematron.full;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.033
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext033 extends TestCase {

    private static final String ERR_MSG = "If a collaboration references a Choreography the value of the attribute " +
            "isClosed must be equal for both elements.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT033_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:collaboration[bpmn:choreographyRef])[1]", 29);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT033_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "033";
    }
}
