package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class Ext028 {

	// TODO: constraint validation

	// @Test
	// public void testConstraintFail() throws Exception {
	// File f = new File(TestHelper.getTestFilePath() + "028" + File.separator
	// + "Fail.bpmn");
	// boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
	// assertFalse(valid);
	// assertEquals(SchematronBPMNValidator.getErrors(),
	// "//bpmn:dataInput[0]: A Sequence Flow must not cross the border of a Pool");
	// }

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "028" + File.separator
				+ "Success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
