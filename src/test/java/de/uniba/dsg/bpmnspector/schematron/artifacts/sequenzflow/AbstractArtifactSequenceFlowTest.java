package de.uniba.dsg.bpmnspector.schematron.artifacts.sequenzflow;

import api.ValidationException;
import api.ValidationResult;
import api.Violation;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Abstract test class for simplifying the testing of the Constraints EXT.006
 * and EXT.007
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
abstract public class AbstractArtifactSequenceFlowTest extends TestCase {

    @Test
    public void testConstraintAssociationFail() throws ValidationException {
        assertTests("Fail_association.bpmn", 11);

    }

    @Test
    public void testConstraintGroupFail() throws ValidationException {
        assertTests("Fail_group.bpmn", 8);
    }

    @Test
    public void testConstraintTextAnnotationFail()
            throws ValidationException {
        assertTests("Fail_text_annotation.bpmn", 8);
    }

    private void assertTests(String fileName, int line)
            throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile(fileName), 3);
        assertFirstViolation(result.getViolations().get(0), fileName);
        assertSecondViolation(result.getViolations().get(1), fileName, line);
        assertThirdViolation(result.getViolations().get(2), fileName);
    }

    protected void assertFirstViolation(Violation v, String fileName) {
        throw new UnsupportedOperationException(
                "must be overriden by every child class!");
    }

    protected void assertSecondViolation(Violation v, String fileName, int line) {
        throw new UnsupportedOperationException(
                "must be overriden by every child class!");
    }

    protected void assertThirdViolation(Violation v, String fileName) {
        throw new UnsupportedOperationException(
                "must be overriden by every child class!");
    }

}
