package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.035
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext035 extends TestCase {

    private static final String ERR_MSG = "A Public process may not be marked as executable.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT035_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:process[@processType='Public'][0]", 3);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT035_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "035";
    }
}
