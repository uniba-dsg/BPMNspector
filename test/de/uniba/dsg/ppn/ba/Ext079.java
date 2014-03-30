package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext079 {

	// FIXME: constraint check should work for every $subProcess

	@Test
	public void testConstraintFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "079\\fail.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:subProcess[0]: InputOutputSpecifications are not allowed in SubProcesses");
	}

	@Test
	public void testConstraintSuccess() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "079\\success.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, true);
		assertEquals(SchematronBPMNValidator.getErrors(), "");
	}
}
