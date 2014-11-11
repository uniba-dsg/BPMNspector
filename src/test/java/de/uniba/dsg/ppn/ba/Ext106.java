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

public class Ext106 {

    private SchematronBPMNValidator validator;

    @Before
    public void setUp() {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    @Test
    public void testConstraintEventFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "106" + File.separator
                + "fail_cancel_end_event.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "A cancel EndEvent is only allowed in a transaction sub-process",
                v.getMessage());
        assertEquals("//bpmn:cancelEventDefinition[0]", v.getxPath());
        assertEquals(9, v.getLine());
    }

    @Test
    public void testConstraintEventRefFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "106" + File.separator
                + "fail_sub_process.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "A cancel EndEvent is only allowed in a transaction sub-process",
                v.getMessage());
        assertEquals("//bpmn:cancelEventDefinition[0]", v.getxPath());
        assertEquals(24, v.getLine());
    }

    @Test
    public void testConstraintCancelBoundaryEventFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "106" + File.separator
                + "fail_cancel_boundary_event.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "A cancel EndEvent is only allowed in a transaction sub-process",
                v.getMessage());
        assertEquals("//bpmn:cancelEventDefinition[0]", v.getxPath());
        assertEquals(18, v.getLine());
    }

    @Test
    public void testConstraintCancelEventSuccess()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "106" + File.separator
                + "success_cancel_event.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintCancelBoundaryEventSuccess()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "106" + File.separator
                + "success_cancel_boundary_event.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

}
