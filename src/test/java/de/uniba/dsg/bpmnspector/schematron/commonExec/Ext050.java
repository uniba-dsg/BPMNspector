package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.046
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext050 extends TestCase {

    private static final String ERR_MSG = "If a ReceiveTask references a message, at most one OutputSet must be defined " +
            "in the ioSpecification.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT050_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:receiveTask[@messageRef][0]", 9);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT050_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "050";
    }
}
