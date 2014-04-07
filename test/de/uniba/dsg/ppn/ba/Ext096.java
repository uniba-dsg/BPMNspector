package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Ext096 {

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
		File f = new File(TestHelper.getTestFilePath() + "096" + File.separator
				+ "Fail.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(validator.getErrors(),
				"//bpmn:startEvent[0]: A Start Event must not have an incoming sequence flow");
	}
}
