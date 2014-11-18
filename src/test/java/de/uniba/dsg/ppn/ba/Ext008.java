package de.uniba.dsg.ppn.ba;

import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext008 extends TestCase {

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
        assertViolation(v, "//bpmn:messageFlow[@targetRef][0]", 7);
    }

    @Override
    protected String getErrorMessage() {
        return "An Artifact MUST NOT be a target for a Message Flow";
    }

    @Override
    protected String getExtNumber() {
        return "008";
    }

}
