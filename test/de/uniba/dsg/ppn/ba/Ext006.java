package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;

public class Ext006 {

	SchematronBPMNValidator validator = null;

	@Before
	public void setUp() throws Exception {
		validator = new SchematronBPMNValidator();
	}

	@After
	public void tearDown() throws Exception {
		validator = null;
	}

	@Test
	public void testConstraintAssociationFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "006" + File.separator
				+ "Fail_association.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(3, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals("An Artifact MUST NOT be a target for a Sequence Flow",
				v.getMessage());
		assertEquals("Fail_association.bpmn", v.getFileName());
		assertEquals("//bpmn:sequenceFlow[@targetRef][0]", v.getxPath());
		assertEquals(7, v.getLine());
		v = result.getViolations().get(1);
		assertEquals(
				"For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target",
				v.getMessage());
		assertEquals("Fail_association.bpmn", v.getFileName());
		assertEquals(
				"//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]",
				v.getxPath());
		assertEquals(11, v.getLine());
		v = result.getViolations().get(2);
		assertEquals(
				"The target element of the sequence flow must reference the SequenceFlow definition using their incoming attribute.",
				v.getMessage());
		assertEquals("Fail_association.bpmn", v.getFileName());
		assertEquals("//bpmn:sequenceFlow[@targetRef][0]", v.getxPath());
		assertEquals(7, v.getLine());
	}

	@Test
	public void testConstraintGroupFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "006" + File.separator
				+ "Fail_group.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(3, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals("An Artifact MUST NOT be a target for a Sequence Flow",
				v.getMessage());
		assertEquals("Fail_group.bpmn", v.getFileName());
		assertEquals("//bpmn:sequenceFlow[@targetRef][0]", v.getxPath());
		assertEquals(7, v.getLine());
		v = result.getViolations().get(1);
		assertEquals(
				"For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target",
				v.getMessage());
		assertEquals("Fail_group.bpmn", v.getFileName());
		assertEquals(
				"//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]",
				v.getxPath());
		assertEquals(8, v.getLine());
		v = result.getViolations().get(2);
		assertEquals(
				"The target element of the sequence flow must reference the SequenceFlow definition using their incoming attribute.",
				v.getMessage());
		assertEquals("Fail_group.bpmn", v.getFileName());
		assertEquals("//bpmn:sequenceFlow[@targetRef][0]", v.getxPath());
		assertEquals(7, v.getLine());
	}

	@Test
	public void testConstraintTextAnnotationFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "006" + File.separator
				+ "Fail_text_annotation.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(3, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals("An Artifact MUST NOT be a target for a Sequence Flow",
				v.getMessage());
		assertEquals("Fail_text_annotation.bpmn", v.getFileName());
		assertEquals("//bpmn:sequenceFlow[@targetRef][0]", v.getxPath());
		assertEquals(7, v.getLine());
		v = result.getViolations().get(1);
		assertEquals(
				"For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target",
				v.getMessage());
		assertEquals("Fail_text_annotation.bpmn", v.getFileName());
		assertEquals(
				"//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]",
				v.getxPath());
		assertEquals(8, v.getLine());
		v = result.getViolations().get(2);
		assertEquals(
				"The target element of the sequence flow must reference the SequenceFlow definition using their incoming attribute.",
				v.getMessage());
		assertEquals("Fail_text_annotation.bpmn", v.getFileName());
		assertEquals("//bpmn:sequenceFlow[@targetRef][0]", v.getxPath());
		assertEquals(7, v.getLine());
	}
}
