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

public class Ext095 {

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
        File f = new File(TestHelper.getTestFilePath() + "095" + File.separator
                + "Fail.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "EventDefinitions defined in a throw event are not allowed to be used somewhere else",
                v.getMessage());
        assertEquals(
                "//bpmn:intermediateThrowEvent/bpmn:messageEventDefinition[0]",
                v.getxPath());
        assertEquals(13, v.getLine());
    }

    @Test
    public void testConstraintEndFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "095" + File.separator
                + "fail_end.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "EventDefinitions defined in a throw event are not allowed to be used somewhere else",
                v.getMessage());
        assertEquals("//bpmn:endEvent/bpmn:messageEventDefinition[0]",
                v.getxPath());
        assertEquals(10, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "095" + File.separator
                + "Success.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
