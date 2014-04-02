package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

public class Ext095 {

	// FIXME: validate other elements

	// @Test
	// public void testConstraintFail() throws Exception {
	// File f = new File(TestHelper.getTestFilePath() + "095" + File.separator
	// + "Fail.bpmn");
	// boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
	// assertFalse(valid);
	// assertEquals(
	// SchematronBPMNValidator.getErrors(),
	// "//bpmn:dataOutput[0]: A DataOutput must be referenced by at least one OutputSet");
	// }

	@Test
	public void testConstraintEndFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "095" + File.separator
				+ "fail_end.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:endEvent/bpmn:messageEventDefinition[0]: EventDefinitions defined in a throw event are only valid within this element");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "095" + File.separator
				+ "Success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertTrue(valid);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
