package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext076 {

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
	public void testConstraintFail1() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076" + File.separator
				+ "Fail_1.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:dataObjectReference[@name][0]: Naming Convention: name = Data Object Name [Data Object Reference State]");
	}

	@Test
	public void testConstraintFail2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076" + File.separator
				+ "Fail_2.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:dataObjectReference[@name][0]: Naming Convention: name = Data Object Name [Data Object Reference State]");
	}

	@Test
	public void testConstraintFail3() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076" + File.separator
				+ "Fail_3.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:dataObjectReference[@name][0]: Naming Convention: name = Data Object Name [Data Object Reference State]");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076" + File.separator
				+ "Success.bpmn");
		boolean valid = validator.validate(f);
		assertTrue(valid);
		assertEquals(validator.getErrors(), "");
	}
}
