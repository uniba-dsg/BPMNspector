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

public class Ext007 {

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
    public void testConstraintAssociationFail() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "007" + File.separator
                + "Fail_association.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals("An Artifact MUST NOT be a source for a Sequence Flow",
                v.getMessage());
        assertEquals("//bpmn:sequenceFlow[@sourceRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source",
                v.getMessage());
        assertEquals(
                "//bpmn:*[./@id = string(//bpmn:sequenceFlow/@sourceRef)][0]",
                v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(2);
        assertEquals(
                "The source element of the sequence flow must reference the SequenceFlow definition using their outgoing attribute.",
                v.getMessage());
        assertEquals("//bpmn:sequenceFlow[@sourceRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintGroupFail() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "007" + File.separator
                + "Fail_group.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals("An Artifact MUST NOT be a source for a Sequence Flow",
                v.getMessage());
        assertEquals("Fail_group.bpmn", v.getFileName());
        assertEquals("//bpmn:sequenceFlow[@sourceRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source",
                v.getMessage());
        assertEquals("Fail_group.bpmn", v.getFileName());
        assertEquals(
                "//bpmn:*[./@id = string(//bpmn:sequenceFlow/@sourceRef)][0]",
                v.getxPath());
        assertEquals(8, v.getLine());
        v = result.getViolations().get(2);
        assertEquals(
                "The source element of the sequence flow must reference the SequenceFlow definition using their outgoing attribute.",
                v.getMessage());
        assertEquals("Fail_group.bpmn", v.getFileName());
        assertEquals("//bpmn:sequenceFlow[@sourceRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintTextAnnotationFail() throws Exception {
        File f = new File(TestHelper.getTestFilePath() + "007" + File.separator
                + "Fail_text_annotation.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals("An Artifact MUST NOT be a source for a Sequence Flow",
                v.getMessage());
        assertEquals("Fail_text_annotation.bpmn", v.getFileName());
        assertEquals("//bpmn:sequenceFlow[@sourceRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source",
                v.getMessage());
        assertEquals("Fail_text_annotation.bpmn", v.getFileName());
        assertEquals(
                "//bpmn:*[./@id = string(//bpmn:sequenceFlow/@sourceRef)][0]",
                v.getxPath());
        assertEquals(8, v.getLine());
        v = result.getViolations().get(2);
        assertEquals(
                "The source element of the sequence flow must reference the SequenceFlow definition using their outgoing attribute.",
                v.getMessage());
        assertEquals("Fail_text_annotation.bpmn", v.getFileName());
        assertEquals("//bpmn:sequenceFlow[@sourceRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

}
