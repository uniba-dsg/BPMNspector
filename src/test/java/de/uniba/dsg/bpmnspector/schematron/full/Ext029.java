package de.uniba.dsg.bpmnspector.schematron.full;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.029
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext029 extends TestCase {

    private static final String ERR_MSG = "The value of the minimum attribute of the participant multiplicity must not be negative.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT029_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:participantMultiplicity[@minimum])[2]", 8);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT029_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "029";
    }
}
