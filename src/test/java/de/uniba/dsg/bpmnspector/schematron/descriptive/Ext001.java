package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationResult;
import api.ValidationException;
import api.Violation;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class for testing Constraint EXT.001
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext001 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintFail2() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("Fail2.bpmn"),
                1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("Success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "001";
    }

    private void assertViolation(Violation v) {
        assertTrue(v.getMessage().contains("Import could not be resolved: "));
        assertTrue(v.getMessage().contains("nofile.bpmn"));
        assertEquals(3, v.getLocation().getLocation().getRow());
        assertEquals("EXT.001", v.getConstraint());
    }
}
