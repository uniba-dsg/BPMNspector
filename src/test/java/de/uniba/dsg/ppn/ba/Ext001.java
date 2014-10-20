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
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class Ext001 {

    SchematronBPMNValidator validator = null;

    @Before
    public void setUp() throws Exception {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    @After
    public void tearDown() throws Exception {
        validator = null;
    }

    @Test
    public void testConstraintFail() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "001" + File.separator
                + "Fail.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals("The imported file does not exist", v.getMessage());
        assertEquals("nofile.bpmn", v.getFileName());
        assertEquals(3, v.getLine());
        assertEquals("EXT.001", v.getConstraint());
    }

    @Test
    public void testConstraintFail2() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "001" + File.separator
                + "Fail2.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals("The imported file does not exist", v.getMessage());
        assertEquals("nofile.bpmn", v.getFileName());
        assertEquals(3, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "001" + File.separator
                + "Success.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
