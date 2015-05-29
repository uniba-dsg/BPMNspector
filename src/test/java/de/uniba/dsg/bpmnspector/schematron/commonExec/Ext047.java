package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.047
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext047 extends TestCase {

    private static final String ERR_MSG = "If a SendTask references a message, at most one DataInput must be defined in " +
            "the ioSpecification.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT047_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:sendTask[@messageRef][0]", 14);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT047_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "047";
    }
}
