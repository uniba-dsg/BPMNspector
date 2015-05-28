package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.024
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext024 extends TestCase {

    private static final String ERR_MSG = "The optional attribute isImmediate must not be 'false' for executable processes.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT024_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:sequenceFlow[@isImmediate='false'][0]", 10);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT024_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "024";
    }
}
