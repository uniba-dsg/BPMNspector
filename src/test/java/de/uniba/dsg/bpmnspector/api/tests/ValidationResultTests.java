package de.uniba.dsg.bpmnspector.api.tests;

import api.*;
import de.uniba.dsg.bpmnspector.BPMNspector;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Matthias Geiger
 */
public class ValidationResultTests {

    private final Path path = Paths.get(System.getProperty("user.dir")).resolve("src").resolve("test").resolve("resources").resolve("036").resolve("fail_call_choreography.bpmn");
    private final Validator validator;

    public ValidationResultTests() throws ValidationException {
        validator = new BPMNspector();
    }

    @Test
    public void testEmptyUnsortedValidationResult() {
        testEmptyValidationResult(new UnsortedValidationResult());
    }

    @Test
    public void testEmptySimpleValidationResult() {
        testEmptyValidationResult(new UnsortedValidationResult());
    }


    private void testEmptyValidationResult(ValidationResult result) {
        assertTrue(result.isValid());
        assertEquals(0, result.getViolations().size());
        assertEquals(0, result.getViolatedConstraints().size());
        assertEquals(0, result.getWarnings().size());
        assertEquals(0, result.getFoundFiles().size());
        assertEquals(0, result.getFilesWithViolations().size());
    }

    @Test
    public void testUnsortedValidationResult() throws ValidationException{
        ValidationResult result = validator.validate(path);
        testValidationResult(result);

        assertEquals("EXT.021", result.getViolations().get(2).getConstraint());

    }

    @Test
    public void testSimpleValidationResult() throws ValidationException{
        ValidationResult result = validator.validate(path);

        ValidationResult sorted = new SimpleValidationResult();
        sorted.addFile(result.getFoundFiles().get(0));

        result.getViolations().stream().forEach(sorted::addViolation);

        assertEquals(result.getViolations().get(2), sorted.getViolations().get(0));
        testValidationResult(sorted);
    }

    private void testValidationResult(ValidationResult result) {
        assertFalse(result.isValid());

        assertEquals(result.getFoundFiles().size(), 1);
        assertEquals(result.getFoundFiles().get(0), path);

        assertEquals(result.getFilesWithViolations().size(), 1);
        assertEquals(result.getFilesWithViolations().get(0), path);

        assertEquals(result.getViolations().size(), 5);

        List<String> expectedConstraints = new LinkedList<>();
        expectedConstraints.add("EXT.021");
        expectedConstraints.add("EXT.022");
        expectedConstraints.add("EXT.036");
        expectedConstraints.add("REF_TYPE");

        assertEquals(result.getViolatedConstraints(), expectedConstraints);
    }


}
