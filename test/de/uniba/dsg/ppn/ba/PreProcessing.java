package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;

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
				+ File.separator + "fail_call_ref_process.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"Referenced process must have at least one None Start Event",
				v.getMessage());
		assertEquals("//bpmn:*[@id ='PROCESS_1'][0]", v.getxPath());
		assertEquals("ref_process.bpmn", v.getFileName());
		assertEquals(3, v.getLine());
	}

	@Test
	public void testConstraintImportedProcessFail1() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "preprocessing"
				+ File.separator + "fail_call_ref_process_call.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(2, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"Referenced process must have at least one None Start Event",
				v.getMessage());
		assertEquals("//bpmn:*[@id ='PROCESS_1'][0]", v.getxPath());
		assertEquals("fail_call_ref_process.bpmn", v.getFileName());
		assertEquals(4, v.getLine());
		v = result.getViolations().get(1);
		assertEquals(
				"Referenced process must have at least one None Start Event",
				v.getMessage());
		assertEquals("//bpmn:*[@id ='PROCESS_1'][0]", v.getxPath());
		assertEquals("ref_process.bpmn", v.getFileName());
		assertEquals(3, v.getLine());
	}

	@Test
	public void testConstraintImportedProcessFail2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "preprocessing"
				+ File.separator + "fail_call_ref_process_call_call.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(2, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals(
				"Referenced process must have at least one None Start Event",
				v.getMessage());
		assertEquals("//bpmn:*[@id ='PROCESS_1'][0]", v.getxPath());
		assertEquals("fail_call_ref_process.bpmn", v.getFileName());
		assertEquals(4, v.getLine());
		v = result.getViolations().get(1);
		assertEquals(
				"Referenced process must have at least one None Start Event",
				v.getMessage());
		assertEquals("//bpmn:*[@id ='PROCESS_1'][0]", v.getxPath());
		assertEquals("ref_process.bpmn", v.getFileName());
		assertEquals(3, v.getLine());
	}

}
