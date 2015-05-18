package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationResult;
import api.ValidationException;
import api.Violation;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.002
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext002 extends TestCase {

    private final static String ERRORMESSAGE = "Files have id duplicates";
    private final static String XPATHSTRING = "/*[local-name() = 'definitions' and namespace-uri() = 'http://www.omg.org/spec/BPMN/20100524/MODEL']/@id";

    @Test
    public void testConstraintImport1Fail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_import.bpmn"), 10);
        assertViolation(result.getViolations().get(0), "fail_import.bpmn", 2);
        assertViolation(result.getViolations().get(1), "import.bpmn", 2);
    }

    @Test
    public void testConstraintImport2Fail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_import2.bpmn"), 10);
        assertViolation(result.getViolations().get(0), "import.bpmn", 2);
        assertViolation(result.getViolations().get(1), "import2.bpmn", 2);
    }

    @Test
    public void testConstraintImport3Fail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_import3.bpmn"), 20);
        assertViolation(result.getViolations().get(0), "fail_import3.bpmn", 2);
        assertViolation(result.getViolations().get(1), "fail_import2.bpmn", 2);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success_import.bpmn"));
    }

    @Override
    protected void assertViolation(Violation v, String fileName, int line) {
        assertViolation(v, ERRORMESSAGE, fileName, XPATHSTRING, line);
    }

    @Override
    protected String getExtNumber() {
        return "002";
    }
}
