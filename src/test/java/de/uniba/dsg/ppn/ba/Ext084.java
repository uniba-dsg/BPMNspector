package de.uniba.dsg.ppn.ba;

import api.ValidationResult;
import api.ValidationException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.084
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext084 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "A DataInput must be referenced by at least one InputSet",
                "//bpmn:dataInput[0]", 5);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "084";
    }
}
