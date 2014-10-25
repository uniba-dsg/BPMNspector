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

public class Ext088 {

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
    public void testConstraintFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "088" + File.separator
                + "fail.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "A DataOutput must be referenced by at least one OutputSet",
                v.getMessage());
        assertEquals("//bpmn:dataOutput[0]", v.getxPath());
        assertEquals(5, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "088" + File.separator
                + "success.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
