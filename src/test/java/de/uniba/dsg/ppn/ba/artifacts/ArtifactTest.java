package de.uniba.dsg.ppn.ba.artifacts;

import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.ppn.ba.TestCase;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class ArtifactTest extends TestCase {

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

    protected void assertViolation(Violation v) {
        throw new UnsupportedOperationException(
                "must be overriden by every child class!");
    }
}
