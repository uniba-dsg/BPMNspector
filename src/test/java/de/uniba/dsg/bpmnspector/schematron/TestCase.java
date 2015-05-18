package de.uniba.dsg.bpmnspector.schematron;

import ch.qos.logback.classic.Level;
import api.ValidationResult;
import api.ValidationException;
import api.Violation;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Abstract test class for all tests of the BPMN Validator to simplify testing
 * and reduce redundancy
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class TestCase {

    protected final SchematronBPMNValidator validator;

    {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    protected static String getTestFilePath() {
        return Paths.get(System.getProperty("user.dir"))
                .resolve("src/test/resources").toString();
    }

    protected File createFile(String fileName) {
        String path = String.format("%s%s%s%s%s", getTestFilePath(),
                File.separator, getExtNumber(), File.separator, fileName);
        return new File(path);
    }

    protected ValidationResult validate(File f) throws ValidationException {
        return validator.validate(f);
    }

    protected void verifyValidResult(File f) throws ValidationException {
        ValidationResult result = validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    protected ValidationResult verifyInvalidResult(File f, int violationsCount)
            throws ValidationException {
        ValidationResult result = validate(f);
        assertFalse(result.isValid());
        assertEquals(violationsCount, result.getViolations().size());
        return result;
    }

    protected void assertViolation(Violation v, String message,
            String fileName, String xpath, int line) {
        assertViolation(v, message, xpath, line);
        assertTrue(v.getLocation().getFileName().getFileName().toString().equals(fileName));
//        assertEquals(fileName, v.getFileName());
    }

    protected void assertViolation(Violation v, String message, String xpath,
            int line) {
        assertEquals(message, v.getMessage());
        assertEquals(xpath, v.getLocation().getXpath().orElse(""));
        assertEquals(line, v.getLocation().getLocation().getRow());
    }

    protected void assertViolation(Violation v, String xpath, int line) {
        assertViolation(v, getErrorMessage(), xpath, line);
    }

    protected String getErrorMessage() {
        throw new UnsupportedOperationException(
                "must be overriden by every child class!");
    }

    protected String getExtNumber() {
        throw new UnsupportedOperationException(
                "must be overriden by every child class!");
    }
}
