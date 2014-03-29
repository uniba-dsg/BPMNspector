package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

public class Ext036 {

	@Test
	public void testConstraintCallChoreographyFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "\\testprocesses\\036\\fail_call_choreography.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(SchematronBPMNValidator.getErrors(),
				"//bpmn:process[0]: A Process must not contain Choreography Activities");
	}

	@Test
	public void testConstraintChoreographyTaskFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "\\testprocesses\\036\\fail_choreography_task.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(SchematronBPMNValidator.getErrors(),
				"//bpmn:process[0]: A Process must not contain Choreography Activities");
	}

	@Test
	public void testConstraintSubChoreographyFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath()
				+ "\\testprocesses\\036\\fail_sub_choreography.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertEquals(valid, false);
		assertEquals(SchematronBPMNValidator.getErrors(),
				"//bpmn:process[0]: A Process must not contain Choreography Activities");
	}

}
