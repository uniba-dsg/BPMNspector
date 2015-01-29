package de.uniba.dsg.ppn.ba;

import api.ValidationResult;
import api.ValidationException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.079
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext079 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(createFile("fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "InputOutputSpecifications are not allowed in SubProcesses",
                "//bpmn:subProcess[0]", 7);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "079";
    }
}
