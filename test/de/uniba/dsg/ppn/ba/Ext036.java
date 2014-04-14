package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext036 {

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
	public void testConstraintCallChoreographyFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "036" + File.separator
				+ "fail_call_choreography.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target\r\n//bpmn:process[0]: A Process must not contain Choreography Activities");
	}

	@Test
	public void testConstraintChoreographyTaskFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "036" + File.separator
				+ "fail_choreography_task.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target\r\n//bpmn:process[0]: A Process must not contain Choreography Activities");
	}

	@Test
	public void testConstraintSubChoreographyFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "036" + File.separator
				+ "fail_sub_choreography.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(
				validator.getErrors(),
				"//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target\r\n//bpmn:process[0]: A Process must not contain Choreography Activities");
	}

}
