package de.uniba.dsg.ppn.ba;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class Ext097 {

    private SchematronBPMNValidator validator;

    @Before
    public void setUp() {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "097" + File.separator
                + "fail_end_without_sub-events.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "A Start event must be present when an End event is used in the same process level",
                v.getMessage());
        assertEquals("//bpmn:endEvent[0]", v.getxPath());
        assertEquals(8, v.getLine());
    }

    @Test
    public void testConstraintSubFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "097" + File.separator
                + "fail_with_sub-endevent.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "A Start event must be present when an End event is used in the same process level",
                v.getMessage());
        assertEquals("//bpmn:endEvent[0]", v.getxPath());
        assertEquals(10, v.getLine());
    }

    @Test
    public void testConstraintSubSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "097" + File.separator
                + "success_with_sub-events.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "097" + File.separator
                + "success_without_sub-events.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
