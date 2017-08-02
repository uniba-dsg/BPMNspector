package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.042
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext042 extends TestCase {

    private static final String ERR_MSG = "If a ServiceTask references an operation, exactly one InputSet must be " +
            "defined in the ioSpecification.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT042_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:serviceTask[@operationRef])[1]", 14);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT042_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "042";
    }
}
