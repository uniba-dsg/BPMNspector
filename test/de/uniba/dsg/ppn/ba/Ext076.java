package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext076 {

	// TODO: constraint validation

	// @Test
	// public void testConstraintFail1() throws Exception {
	// File f = new File(TestHelper.getTestFilePath() + "076\\fail_1.bpmn");
	// boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
	// assertEquals(valid, false);
	// assertEquals(
	// SchematronBPMNValidator.getErrors(),
	// "//bpmn:dataObjectReference[@name][0]: Naming Convention: name = Data Object Name [Data Object Reference State]");
	// }
	//
	// @Test
	// public void testConstraintFail2() throws Exception {
	// File f = new File(TestHelper.getTestFilePath() + "076\\fail_2.bpmn");
	// boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
	// assertEquals(valid, false);
	// assertEquals(
	// SchematronBPMNValidator.getErrors(),
	// "//bpmn:dataObjectReference[@name][0]: Naming Convention: name = Data Object Name [Data Object Reference State]");
	// }
	//
	// @Test
	// public void testConstraintFail3() throws Exception {
	// File f = new File(TestHelper.getTestFilePath() + "076\\fail_3.bpmn");
	// boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
	// assertEquals(valid, false);
	// assertEquals(
	// SchematronBPMNValidator.getErrors(),
	// "//bpmn:dataObjectReference[@name][0]: Naming Convention: name = Data Object Name [Data Object Reference State]");
	// }

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "076\\success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, true);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
