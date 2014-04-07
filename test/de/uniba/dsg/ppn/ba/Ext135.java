package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext135 {

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
	public void testConstraintFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "135" + File.separator
				+ "fail.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:parallelGateway[0]: A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows\r\n//bpmn:parallelGateway[1]: A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows");
	}

	@Test
	public void testConstraintSubFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "135" + File.separator
				+ "fail_no_connection.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:parallelGateway[0]: A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows");
	}

	@Test
	public void testConstraintEXSubFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "135" + File.separator
				+ "fail_ex_no_connection.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:exclusiveGateway[0]: A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows");
	}

	@Test
	public void testConstraintBothMultipleSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "135" + File.separator
				+ "success_multiple_in_and_out.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}

	@Test
	public void testConstraintOutMultipleSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "135" + File.separator
				+ "success_multiple_out.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}
}
