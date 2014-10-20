package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class Ext102 {

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
        File f = new File(TestHelper.getTestFilePath() + "102" + File.separator
                + "Fail.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "A message flow must connect ’InteractionNodes’ from different Pools",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[0]", v.getxPath());
        assertEquals(7, v.getLine());
        v = result.getViolations().get(1);
        assertEquals("A Start Event MUST NOT be a source for a message flow",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[@sourceRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }
}
