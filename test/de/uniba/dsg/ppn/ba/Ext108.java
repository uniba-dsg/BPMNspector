package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.Test;

public class Ext108 {

	@Test
	public void testConstraintFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "108" + File.separator
				+ "Fail.bpmn");
		boolean valid = SchematronBPMNValidator.validateViaPureSchematron(f);
		assertFalse(valid);
		assertEquals(
				SchematronBPMNValidator.getErrors(),
				"//bpmn:messageFlow[0]: A message flow must connect ’InteractionNodes’ from different Pools\r\n//bpmn:messageFlow[@targetRef][0]: An End Event MUST NOT be a target for a message flow");
	}
}
