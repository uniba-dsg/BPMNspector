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

public class Ext135 {

    private SchematronBPMNValidator validator;
    private final static String ERRORMESSAGE = "A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows";

    @Before
    public void setUp() {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "135" + File.separator
                + "fail.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals("//bpmn:parallelGateway[0]", v.getxPath());
        assertEquals(10, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGE, v.getMessage());
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
        assertEquals(ERRORMESSAGE, v.getMessage());
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
        assertEquals(ERRORMESSAGE, v.getMessage());
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
