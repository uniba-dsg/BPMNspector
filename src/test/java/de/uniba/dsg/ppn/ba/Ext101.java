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

public class Ext101 {

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
        File f = new File(TestHelper.getTestFilePath() + "101" + File.separator
                + "fail.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "The source element of the sequence flow must reference the SequenceFlow definition using their outgoing attribute.",
                v.getMessage());
        assertEquals("//bpmn:sequenceFlow[@sourceRef][0]", v.getxPath());
        assertEquals(9, v.getLine());
        v = result.getViolations().get(1);
        assertEquals("A startEvent must have a outgoing subelement",
                v.getMessage());
        assertEquals("//bpmn:startEvent[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "101" + File.separator
                + "success.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
