package de.uniba.dsg.ppn.ba.artifacts;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.ppn.ba.TestCase;
import org.junit.Test;

/**
 * Abstract test class for simplifying the testing of the Constraints EXT.008
 * and EXT.009
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
abstract public class AbstractArtifactTest extends TestCase {

    @Test
    public void testConstraintAssociationFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_association.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintGroupFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_group.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintTextAnnotationFail()
            throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_text_annotation.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    protected void assertViolation(Violation v) {
        throw new UnsupportedOperationException(
                "must be overriden by every child class!");
    }
}
