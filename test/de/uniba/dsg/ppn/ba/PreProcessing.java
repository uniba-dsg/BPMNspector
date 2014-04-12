package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PreProcessing {

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
	public void testConstraintImportedProcessFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "preprocessing"
				+ File.separator + "fail_call_ref_process_call.bpmn");
		boolean valid = validator.validate(f);
		assertFalse(valid);
		assertEquals(
				validator.getErrors(),
				"//bpmn:process[./@id = string(//bpmn:callActivity/@calledElement)][0]: Referenced process must have at least one None Start Event");
	}

}
