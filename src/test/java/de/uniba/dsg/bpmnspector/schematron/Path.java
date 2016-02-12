package de.uniba.dsg.bpmnspector.schematron;

import api.ValidationException;
import api.ValidationResult;
import api.Warning;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
    public void testInvalidUrlImports() throws ValidationException {
        ValidationResult result = validate(createFile("import_URL.bpmn"));
        assertEquals(2, result.getViolations().size());
    }

    @Override
    protected String getExtNumber() {
        return "path";
    }
}
