package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.053
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext053 extends TestCase {

    private static final String ERR_MSG = "If a script is present the script type must be defined.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT053_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:scriptTask[bpmn:script][0]", 7);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT053_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "053";
    }
}
