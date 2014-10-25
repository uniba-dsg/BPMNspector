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

public class Ext146 {

    private SchematronBPMNValidator validator = null;
    private final static String ERRORMESSAGE = "Only messageEventDefininitions, escalationEventDefinitions, errorEventDefinitions, cancelEventDefinitions, compensationEventDefinitions, signalEventDefinitions and terminateEventDefinitions are allowed for end events";
    private final static String XPATHSTRING = "//bpmn:endEvent[0]";

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
    public void testConstraintLinkFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
                + "fail_link.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintTimerFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
                + "fail_timer.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintTimerRefFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
                + "fail_timer_ref.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(8, v.getLine());
    }

    @Test
    public void testConstraintMultipleFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
                + "fail_multiple.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintConditionalFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "146" + File.separator
                + "fail_conditional.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertTrue(v.getMessage().contains("cvc-complex-type.2.4.b"));
        assertTrue(v.getMessage().contains("conditionalEventDefinition"));
        assertEquals("XSD-Check", v.getConstraint());
        assertEquals(9, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
                + "success_message.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintMultipleSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "098" + File.separator
                + "success_multiple.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

}
