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

public class Ext103 {

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
        File f = new File(TestHelper.getTestFilePath() + "103" + File.separator
                + "Fail.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "If a Start Event is target of a MessageFlow definition, at least one messageEventDefinition must be present",
                v.getMessage());
        assertEquals(
                "//bpmn:startEvent[@id = string(//bpmn:messageFlow/@targetRef)][0]",
                v.getxPath());
        assertEquals(13, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "103" + File.separator
                + "Success.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintRefSuccess() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "103" + File.separator
                + "Success_ref.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
