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

public class Ext135 {

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
        File f = new File(TestHelper.getTestFilePath() + "135" + File.separator
                + "fail.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows",
                v.getMessage());
        assertEquals("//bpmn:parallelGateway[0]", v.getxPath());
        assertEquals(10, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(
                "A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows",
                v.getMessage());
        assertEquals("//bpmn:parallelGateway[1]", v.getxPath());
        assertEquals(20, v.getLine());
    }

    @Test
    public void testConstraintSubFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "135" + File.separator
                + "fail_no_connection.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows",
                v.getMessage());
        assertEquals("//bpmn:parallelGateway[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }

    @Test
    public void testConstraintEXSubFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "135" + File.separator
                + "fail_ex_no_connection.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows",
                v.getMessage());
        assertEquals("//bpmn:exclusiveGateway[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }

    @Test
    public void testConstraintBothMultipleSuccess()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "135" + File.separator
                + "success_multiple_in_and_out.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintOutMultipleSuccess()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "135" + File.separator
                + "success_multiple_out.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
