package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext007 {

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
		File f = new File(TestHelper.getTestFilePath() + "007" + File.separator
				+ "Fail_association.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:sequenceFlow[@sourceRef][0]: An Artifact MUST NOT be a source for a Sequence Flow\r\n//bpmn:*[./@id = string(//bpmn:sequenceFlow/@sourceRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source\r\n//bpmn:sequenceFlow[@sourceRef][0]: The source element of the sequence flow must reference the SequenceFlow definition using their outcoming attribute.");
	}

	@Test
	public void testConstraintGroupFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "007" + File.separator
				+ "Fail_group.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:sequenceFlow[@sourceRef][0]: An Artifact MUST NOT be a source for a Sequence Flow\r\n//bpmn:*[./@id = string(//bpmn:sequenceFlow/@sourceRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source\r\n//bpmn:sequenceFlow[@sourceRef][0]: The source element of the sequence flow must reference the SequenceFlow definition using their outcoming attribute.");
	}

	@Test
	public void testConstraintTextAnnotationFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "007" + File.separator
				+ "Fail_text_annotation.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:sequenceFlow[@sourceRef][0]: An Artifact MUST NOT be a source for a Sequence Flow\r\n//bpmn:*[./@id = string(//bpmn:sequenceFlow/@sourceRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source\r\n//bpmn:sequenceFlow[@sourceRef][0]: The source element of the sequence flow must reference the SequenceFlow definition using their outcoming attribute.");
	}

}
