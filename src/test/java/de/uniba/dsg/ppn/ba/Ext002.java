package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext002 extends TestCase {

    private final static String ERRORMESSAGE = "Files have id duplicates";
    private final static String XPATHSTRING = "//bpmn:*[@id = 'PROCESS_1'][0]";

    @Test
    public void testConstraintImport1Fail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_import.bpmn"), 8);
        assertViolation(result.getViolations().get(0), "fail_import.bpmn", 4);
        assertViolation(result.getViolations().get(1), "import.bpmn", 3);
    }

    @Test
    public void testConstraintImport2Fail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_import2.bpmn"), 8);
        assertViolation(result.getViolations().get(0), "import.bpmn", 3);
        assertViolation(result.getViolations().get(1), "import2.bpmn", 3);
    }

    @Test
    public void testConstraintImport3Fail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_import3.bpmn"), 16);
        assertViolation(result.getViolations().get(0), "fail_import3.bpmn", 4);
        assertViolation(result.getViolations().get(1), "fail_import2.bpmn", 5);
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success_import.bpmn"));
    }

    private void assertViolation(Violation v, String fileName, int line) {
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(fileName, v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(line, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "002";
    }
}
