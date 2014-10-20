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

public class Ext107 {

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
        File f = new File(TestHelper.getTestFilePath() + "107" + File.separator
                + "fail.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "The target element of the sequence flow must reference the SequenceFlow definition using their incoming attribute.",
                v.getMessage());
        assertEquals("//bpmn:sequenceFlow[@targetRef][0]", v.getxPath());
        assertEquals(9, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(
                "An End Event MUST have at least one incoming Sequence Flow",
                v.getMessage());
        assertEquals("//bpmn:endEvent[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "107" + File.separator
                + "success.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
