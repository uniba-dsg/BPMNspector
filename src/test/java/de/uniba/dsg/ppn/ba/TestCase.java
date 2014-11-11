package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Paths;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class TestCase {

    protected SchematronBPMNValidator validator;

    {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    private static String getTestFilePath() {
        return Paths.get(System.getProperty("user.dir"))
                .resolve("src/test/resources").toString();
    }

    protected File createFile(String fileName) {
        String path = String.format("%s%s%s%s%s", getTestFilePath(),
                File.separator, getExtNumber(), File.separator, fileName);
        return new File(path);
    }

    protected ValidationResult validate(File f) throws BpmnValidationException {
        return validator.validate(f);
    }

    protected void verifyValidResult(File f) throws BpmnValidationException {
        ValidationResult result = validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    protected ValidationResult verifyInValidResult(File f, int violationsCount)
            throws BpmnValidationException {
        ValidationResult result = validate(f);
        assertFalse(result.isValid());
        assertEquals(violationsCount, result.getViolations().size());
        return result;
    }

    protected String getExtNumber() {
        throw new NotImplementedException();
    }

}
