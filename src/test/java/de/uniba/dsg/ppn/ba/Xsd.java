package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class Xsd {

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
	public void testXsdFail() throws Exception {
		File f = new File(TestHelper.getTestFilePath() + "xsd" + File.separator
				+ "xsdfail.bpmn");
		ValidationResult result = validator.validate(f);
		assertFalse(result.isValid());
		assertEquals(1, result.getViolations().size());
		Violation v = result.getViolations().get(0);
		assertEquals("xsdfail.bpmn", v.getFileName());
		assertEquals(6, v.getLine());
		assertTrue(v
				.getMessage()
				.contains(
						"cvc-complex-type.2.4.a: Ungültiger Content wurde beginnend mit Element \"outgoing\" gefunden."));
		assertEquals("", v.getxPath());
		assertEquals("XSD-Check", v.getConstraint());
	}
}
