package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class Ext002 {

    private SchematronBPMNValidator validator = null;
    private final static String ERRORMESSAGE = "Files have id duplicates";
    private final static String XPATHSTRING = "//bpmn:*[@id = 'PROCESS_1'][0]";

    @Before
    public void setUp() {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    @After
    public void tearDown() {
        validator = null;
    }

    @Test
    public void testConstraintImport1Fail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "002" + File.separator
                + "fail_import.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(8, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals("fail_import.bpmn", v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(4, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals("import.bpmn", v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(3, v.getLine());
    }

    @Test
    public void testConstraintImport2Fail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "002" + File.separator
                + "fail_import2.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(8, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals("import.bpmn", v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(3, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals("import2.bpmn", v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(3, v.getLine());
    }

    @Test
    public void testConstraintImport3Fail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "002" + File.separator
                + "fail_import3.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(16, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals("fail_import3.bpmn", v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(4, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals("fail_import2.bpmn", v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(5, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "002" + File.separator
                + "success_import.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
