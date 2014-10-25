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

public class Ext099 {

    SchematronBPMNValidator validator = null;

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
    public void testConstraintEventFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
                + "fail_event.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "Referenced process must have at least one None Start Event",
                v.getMessage());
        assertEquals(
                "//bpmn:process[./@id = //bpmn:callActivity/@calledElement][0]",
                v.getxPath());
        assertEquals(6, v.getLine());
    }

    @Test
    public void testConstraintEventRefFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
                + "fail_eventref.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "Referenced process must have at least one None Start Event",
                v.getMessage());
        assertEquals(
                "//bpmn:process[./@id = //bpmn:callActivity/@calledElement][0]",
                v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintImportedProcessFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
                + "fail_call_ref_process.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "Referenced process must have at least one None Start Event",
                v.getMessage());
        assertEquals("//bpmn:*[@id = 'PROCESS_1'][0]", v.getxPath());
        assertEquals(3, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
                + "success.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintGlobalSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "099" + File.separator
                + "success_global.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
