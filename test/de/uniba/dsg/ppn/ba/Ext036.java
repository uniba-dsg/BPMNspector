package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Test;

public class Ext036 {

	@Test
	public void testConstraintCallChoreographyFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "036" + File.separator
				+ "fail_call_choreography.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target\r\n//bpmn:process[0]: A Process must not contain Choreography Activities");
	}

	@Test
	public void testConstraintChoreographyTaskFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "036" + File.separator
				+ "fail_choreography_task.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target\r\n//bpmn:process[0]: A Process must not contain Choreography Activities");
	}

	@Test
	public void testConstraintSubChoreographyFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "036" + File.separator
				+ "fail_sub_choreography.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:*[./@id = string(//bpmn:sequenceFlow/@targetRef)][0]: For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target\r\n//bpmn:process[0]: A Process must not contain Choreography Activities");
	}

}
