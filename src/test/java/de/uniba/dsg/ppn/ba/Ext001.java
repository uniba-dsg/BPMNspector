package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

/**
 * Test class for testing Constraint EXT.001
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext001 extends TestCase {

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintFail2() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(createFile("Fail2.bpmn"),
                1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("Success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "001";
    }

    private void assertViolation(Violation v) {
        assertTrue(v.getMessage().contains("Import could not be resolved: "));
        assertTrue(v.getMessage().contains("nofile.bpmn"));
        assertEquals(3, v.getLine());
        assertEquals("EXT.001", v.getConstraint());
    }
}
