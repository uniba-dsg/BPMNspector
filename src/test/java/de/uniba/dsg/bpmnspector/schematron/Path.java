package de.uniba.dsg.bpmnspector.schematron;

import api.ValidationException;
import api.ValidationResult;
import api.Warning;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Test class for testing the right resolvement of pathes
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Path extends TestCase {

    @Test
    public void testConstraintSuccess1() throws ValidationException {
        File f = new File(getTestFilePath() + File.separator + "path"
                + File.separator + "folder" + File.separator
                + "success_import.bpmn");
        verifyValidResult(f);
    }

    @Test
    public void testConstraintSuccess2() throws ValidationException {
        verifyValidResult(createFile("success_import.bpmn"));
    }

    @Test
    public void testWarningIfUrlUsed() throws ValidationException {
        ValidationResult result = validate(createFile("import_URL.bpmn"));
        assertEquals(result.getWarnings().size(), 1);
        Warning warning  = result.getWarnings().get(0);
        assertEquals(warning.getMessage(), "Imports using URLs are currently not supported.");
        assertEquals(warning.getLocation().getLocation().getRow(), 3);
    }

    @Override
    protected String getExtNumber() {
        return "path";
    }
}
