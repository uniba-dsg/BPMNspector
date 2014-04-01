package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class Ext076 {

	@Test
	public void testConstraintFail1() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076" + File.separator
				+ "Fail_1.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:dataObjectReference[@name][0]: Naming Convention: name = Data Object Name [Data Object Reference State]");
	}

	@Test
	public void testConstraintFail2() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076" + File.separator
				+ "Fail_2.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:dataObjectReference[@name][0]: Naming Convention: name = Data Object Name [Data Object Reference State]");
	}

	@Test
	public void testConstraintFail3() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076" + File.separator
				+ "Fail_3.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:dataObjectReference[@name][0]: Naming Convention: name = Data Object Name [Data Object Reference State]");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076" + File.separator
				+ "Success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
