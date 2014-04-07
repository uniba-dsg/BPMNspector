package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext031 {

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
	public void testConstraintCircleFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
				+ "Fail_circle.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:messageFlow[0]: A message flow must connect ’InteractionNodes’ from different Pools");
	}

	@Test
	public void testConstraintFromPoolFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
				+ "Fail_message_flow_from_pool.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:messageFlow[0]: A message flow must connect ’InteractionNodes’ from different Pools\r\n//bpmn:messageFlow[@targetRef][0]: An End Event MUST NOT be a target for a message flow");
	}

	@Test
	public void testConstraintToPoolFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
				+ "Fail_message_flow_to_pool.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:messageFlow[0]: A message flow must connect ’InteractionNodes’ from different Pools\r\n//bpmn:messageFlow[@sourceRef][0]: A Start Event MUST NOT be a source for a message flow");
	}

	@Test
	public void testConstraintSamePoolFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
				+ "Fail_message_flow_in_same_pool.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:messageFlow[0]: A message flow must connect ’InteractionNodes’ from different Pools\r\n//bpmn:messageFlow[@sourceRef][0]: A Start Event MUST NOT be a source for a message flow\r\n//bpmn:messageFlow[@targetRef][0]: An End Event MUST NOT be a target for a message flow");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "031" + File.separator
				+ "Success.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}
}
