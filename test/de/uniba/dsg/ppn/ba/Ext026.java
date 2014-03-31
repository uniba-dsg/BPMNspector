package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext026 {

	// TODO: test all elements
	// TODO: add error messages assert

	@Test
	public void testConstraintActivityFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "026\\fail_activity.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		// assertEquals(SchematronBPMNValidator.getErrors(), "");
	}

	// @Test
	// public void testConstraintGatewayFail() throws Exception {
	// File f = new File(TestHelper.getTestFilePath()
	// + "026\\fail_gateway.bpmn");
	// boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
	// assertEquals(valid, false);
	// assertEquals(SchematronBPMNValidator.getErrors(), "");
	// }

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "026\\success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, true);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
