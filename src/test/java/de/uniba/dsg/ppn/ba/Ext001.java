package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

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
        assertEquals("The imported file does not exist", v.getMessage());
        assertEquals("nofile.bpmn", v.getFileName());
        assertEquals(3, v.getLine());
        assertEquals("EXT.001", v.getConstraint());
    }
}
