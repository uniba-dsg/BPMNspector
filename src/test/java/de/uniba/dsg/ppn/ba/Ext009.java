package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext009 extends TestCase {

    @Test
    public void testConstraintAssociationFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_association.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintGroupFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_group.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintTextAnnotationFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_text_annotation.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    private void assertViolation(Violation v) {
        assertEquals("An Artifact MUST NOT be a source for a Message Flow",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[@sourceRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "009";
    }
}
