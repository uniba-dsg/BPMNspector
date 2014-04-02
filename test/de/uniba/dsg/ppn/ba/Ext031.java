package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class Ext031 {

	@Test
	public void testConstraintCircleFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
				+ "Fail_circle.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:messageFlow[0]: A message flow must connect ’InteractionNodes’ from different Pools");
	}

	@Test
	public void testConstraintFromPoolFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
				+ "Fail_message_flow_from_pool.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:messageFlow[0]: A message flow must connect ’InteractionNodes’ from different Pools\r\n//bpmn:messageFlow[@targetRef][0]: An End Event MUST NOT be a target for a message flow");
	}

	@Test
	public void testConstraintToPoolFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
				+ "Fail_message_flow_to_pool.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:messageFlow[0]: A message flow must connect ’InteractionNodes’ from different Pools\r\n//bpmn:messageFlow[@sourceRef][0]: A Start Event MUST NOT be a source for a message flow");
	}

	@Test
	public void testConstraintSamePoolFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
				+ "Fail_message_flow_in_same_pool.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:messageFlow[0]: A message flow must connect ’InteractionNodes’ from different Pools\r\n//bpmn:messageFlow[@sourceRef][0]: A Start Event MUST NOT be a source for a message flow\r\n//bpmn:messageFlow[@targetRef][0]: An End Event MUST NOT be a target for a message flow");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
				+ "Success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
