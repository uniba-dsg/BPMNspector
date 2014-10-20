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

public class Ext031 {

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
    public void testConstraintCircleFail() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
                + "Fail_circle.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "A message flow must connect ’InteractionNodes’ from different Pools",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintFromPoolFail() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
                + "Fail_message_flow_from_pool.bpmn");
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
        assertEquals("An End Event MUST NOT be a target for a message flow",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[@targetRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintToPoolFail() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
                + "Fail_message_flow_to_pool.bpmn");
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

    @Test
    public void testConstraintSamePoolFail() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
                + "Fail_message_flow_in_same_pool.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
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
        v = result.getViolations().get(2);
        assertEquals("An End Event MUST NOT be a target for a message flow",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[@targetRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
                + "Success.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
