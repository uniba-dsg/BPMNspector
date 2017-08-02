package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
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
        ValidationResult result = verifyInvalidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "A DataInput must be referenced by at least one InputSet",
                "(//bpmn:dataInput)[1]", 5);
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
