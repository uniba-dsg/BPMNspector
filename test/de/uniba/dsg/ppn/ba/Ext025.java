package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class Ext025 {

	@Test
	public void testConstraintNoIncomingFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "025" + File.separator
				+ "fail.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:sequenceFlow[bpmn:conditionExpression] [not(string(@sourceRef)=//bpmn:exclusiveGateway/@id)] [not(string(@sourceRef)=//bpmn:parallelGateway/@id)] [not(string(@sourceRef)=//bpmn:inclusiveGateway/@id)] [not(string(@sourceRef)=//bpmn:complexGateway/@id)] [not(string(@sourceRef)=//bpmn:eventBasedGateway/@id)][0]: An Activity must not have only one outgoing conditional sequence flow if conditionExpression is present");
	}

	@Test
	public void testConstraintNoIncomingFail2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "025" + File.separator
				+ "fail_2.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:sequenceFlow[bpmn:conditionExpression] [not(string(@sourceRef)=//bpmn:exclusiveGateway/@id)] [not(string(@sourceRef)=//bpmn:parallelGateway/@id)] [not(string(@sourceRef)=//bpmn:inclusiveGateway/@id)] [not(string(@sourceRef)=//bpmn:complexGateway/@id)] [not(string(@sourceRef)=//bpmn:eventBasedGateway/@id)][0]: An Activity must not have only one outgoing conditional sequence flow if conditionExpression is present");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "025" + File.separator
				+ "success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}

	@Test
	public void testConstraintSuccessNoCondition() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "025" + File.separator
				+ "success_no_condition.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
