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

public class Ext025 {

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
    public void testConstraintNoIncomingFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "025" + File.separator
                + "fail.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "An Activity must not have only one outgoing conditional sequence flow if conditionExpression is present",
                v.getMessage());
        assertEquals(
                "//bpmn:sequenceFlow[bpmn:conditionExpression] [not(@sourceRef = //bpmn:exclusiveGateway/@id)] [not(@sourceRef = //bpmn:parallelGateway/@id)] [not(@sourceRef = //bpmn:inclusiveGateway/@id)] [not(@sourceRef = //bpmn:complexGateway/@id)] [not(@sourceRef = //bpmn:eventBasedGateway/@id)][0]",
                v.getxPath());
        assertEquals(19, v.getLine());
    }

    @Test
    public void testConstraintNoIncomingFail2() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "025" + File.separator
                + "fail_2.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "An Activity must not have only one outgoing conditional sequence flow if conditionExpression is present",
                v.getMessage());
        assertEquals(
                "//bpmn:sequenceFlow[bpmn:conditionExpression] [not(@sourceRef = //bpmn:exclusiveGateway/@id)] [not(@sourceRef = //bpmn:parallelGateway/@id)] [not(@sourceRef = //bpmn:inclusiveGateway/@id)] [not(@sourceRef = //bpmn:complexGateway/@id)] [not(@sourceRef = //bpmn:eventBasedGateway/@id)][0]",
                v.getxPath());
        assertEquals(19, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "025" + File.separator
                + "success.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintSuccess2() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "025" + File.separator
                + "success_2.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintSuccessNoCondition()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "025" + File.separator
                + "success_no_condition.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
