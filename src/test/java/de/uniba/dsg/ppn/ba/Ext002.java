package de.uniba.dsg.ppn.ba;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import de.uniba.dsg.bpmnspector.common.Violation;
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
    private final static String XPATHSTRING = "//bpmn:*[@id = 'PROCESS_1'][0]";

    @Test
    public void testConstraintImport1Fail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_import.bpmn"), 8);
        assertViolation(result.getViolations().get(0), "fail_import.bpmn", 4);
        assertViolation(result.getViolations().get(1), "import.bpmn", 3);
    }

    @Test
    public void testConstraintImport2Fail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_import2.bpmn"), 8);
        assertViolation(result.getViolations().get(0), "import.bpmn", 3);
        assertViolation(result.getViolations().get(1), "import2.bpmn", 3);
    }

    @Test
    public void testConstraintImport3Fail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_import3.bpmn"), 16);
        assertViolation(result.getViolations().get(0), "fail_import3.bpmn", 4);
        assertViolation(result.getViolations().get(1), "fail_import2.bpmn", 5);
    }

    @Test
    public void testConstraintSuccess() throws ValidatorException {
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
