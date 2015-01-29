package de.uniba.dsg.ppn.ba.artifacts;

import api.ValidationResult;
import api.ValidationException;
import api.Violation;
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
    public void testConstraintAssociationFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_association.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintGroupFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_group.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintTextAnnotationFail()
            throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_text_annotation.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    protected void assertViolation(Violation v) {
        throw new UnsupportedOperationException(
                "must be overriden by every child class!");
    }
}
