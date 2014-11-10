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
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class Ext036 {

    private SchematronBPMNValidator validator;

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
    public void testConstraintCallChoreographyFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "036" + File.separator
                + "fail_call_choreography.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source",
                v.getMessage());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@sourceRef][1]",
                v.getxPath());
        assertEquals(10, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target",
                v.getMessage());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][1]",
                v.getxPath());
        assertEquals(10, v.getLine());
        v = result.getViolations().get(2);
        assertEquals("A Process must not contain Choreography Activities",
                v.getMessage());
        assertEquals("//bpmn:process[0]", v.getxPath());
        assertEquals(3, v.getLine());
    }

    @Test
    public void testConstraintChoreographyTaskFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "036" + File.separator
                + "fail_choreography_task.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source",
                v.getMessage());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@sourceRef][1]",
                v.getxPath());
        assertEquals(10, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target",
                v.getMessage());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][1]",
                v.getxPath());
        assertEquals(10, v.getLine());
        v = result.getViolations().get(2);
        assertEquals("A Process must not contain Choreography Activities",
                v.getMessage());
        assertEquals("//bpmn:process[0]", v.getxPath());
        assertEquals(3, v.getLine());
    }

    @Test
    public void testConstraintSubChoreographyFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "036" + File.separator
                + "fail_sub_choreography.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source",
                v.getMessage());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@sourceRef][1]",
                v.getxPath());
        assertEquals(10, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target",
                v.getMessage());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][1]",
                v.getxPath());
        assertEquals(10, v.getLine());
        v = result.getViolations().get(2);
        assertEquals("A Process must not contain Choreography Activities",
                v.getMessage());
        assertEquals("//bpmn:process[0]", v.getxPath());
        assertEquals(3, v.getLine());
    }

}
