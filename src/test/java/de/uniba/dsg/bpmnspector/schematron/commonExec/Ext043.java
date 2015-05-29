package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.043
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext043 extends TestCase {

    private static final String ERR_MSG = "If a ServiceTask references an operation, at most one OutputSet can be " +
            "defined in the ioSpecification.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT043_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:serviceTask[@operationRef][0]", 16);
    }

    @Override
    protected String getExtNumber() {
        return "043";
    }
}
