package de.uniba.dsg.bpmnspector.refcheck.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidator;
import de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl;
import de.uniba.dsg.bpmnspector.refcheck.ValidatorException;

/**
 * This test class tests the validator with the predefined BPMN files. The
 * predefinitions of the tests can be looked up in the bachelor thesis with the
 * T numbers. The tests are jUnit tests.
 * 
 * @author Andreas Vorndran
 * @version 1.0
 * 
 */
public class TestBPMNFiles {

	private static BPMNReferenceValidator application;

	@BeforeClass
	public static void setupBeforeClass() throws ValidatorException {
		application = new BPMNReferenceValidatorImpl();
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T1.
	 * 
	 * @throws ValidatorException
	 */
	@Test
	public void testValidateWithT1() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-1-gruppe-c.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
				
		validateResultType(result.getViolations().get(0), 11, "message", "participant");
		
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T2.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT2() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-2-gruppe-d.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 3, "itemRef", "participant");
		
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T3.
	 * 
	 * @throws ValidatorException
	 */
	@Test
	public void testValidateWithT3() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-3-gruppe-e.bpmn");

		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 2);
		
		validateResultType(result.getViolations().get(0), 32, "sourceRef", "participant");
		validateResultType(result.getViolations().get(1), 33, "targetRef", "sequenceFlow");
		
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T4.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT4() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-4-gruppe-f.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 2);
		
		validateResultType(result.getViolations().get(0), 10, "incoming", "participant");
		validateResultType(result.getViolations().get(1), 30, "dafault", "startEvent");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T5.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT5() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-5-gruppe-g.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 11, "interfaceRef", "task");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T6.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT6() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-6-gruppe-h.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 23, "participantRef", "sequenceFlow");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T7.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT7() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-7-gruppe-i1.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 2);
		
		validateResultType(result.getViolations().get(0), 11, "sourceRef", "message");
		validateResultType(result.getViolations().get(1), 11, "targetRef", "process");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T8.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT8() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-8-gruppe-l.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 22, "operationRef", "message");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T9.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT9() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-9-gruppe-m.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 18, "dataInputRefs", "participant");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T10.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT10() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-10-gruppe-n.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 19, "dataOutputRefs", "participant");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T11.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT11() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-11-gruppe-o.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 2);
		
		validateResultType(result.getViolations().get(0), 23, "sourceRef", "participant");
		validateResultType(result.getViolations().get(1), 44, "targetRef", "startEvent");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T12.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT12() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-12-gruppe-p.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 27, "eventDefinitionRef", "sequenceFlow");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T13.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT13() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-13-gruppe-q.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 19, "attachedToRef", "participant");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T14.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT14() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-14-gruppe-r.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 2);
		
		validateResultType(result.getViolations().get(0), 24, "source", "startEvent");
		validateResultType(result.getViolations().get(1), 43, "target", "endEvent");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T15.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT15() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-15-gruppe-s.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 18, "definitionalCollaborationRef", "message");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T16.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT16() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-16-gruppe-t.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 23, "correlationPropertyRef", "participant");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T17.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT17() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-17-gruppe-u.bpmn");
		
		assertEquals(1, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 8, "errorRef", "message");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T18.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT18() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-18-referenz-6-teil-2.bpmn");
		
		assertEquals(2, result.getCheckedFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 10, "categoryValueRef", "participant");
	}

	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T18 using a reference to a Subfolder.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT18Subfolder() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-18-referenz-6-teil-2-subfolder.bpmn");
		assertEquals(2, result.getCheckedFiles().size());
				
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 10, "categoryValueRef", "participant");
	}
	
//	commented out as absolute path is system dependent
//	/**
//	 * Test method for
//	 * {@link de.uniba.wiai.lspi.ws1213.ba.application.BPMNReferenceValidatorImpl#validate(java.lang.String)}
//	 * with T18 using a reference with an absolute path.
//	 * 
//	 * @throws ValidatorException
//	 * 
//	 */
//	@Test
//	public void testValidateWithT18Absolute() throws ValidatorException {
//		ValidationResult result = application
//				.validate("src/test/resources/test-18-referenz-6-teil-2-absolute.bpmn");
//		assertEquals(2, result.getCheckedFiles().size());
//		assertFalse(result1.isValid());
//		List<Violation> errors = result1.getViolations();
//		assertEquals(1, errors.size());
//		TypeViolation foundError = (TypeViolation) errors.get(0);
//		TypeViolation expectedError = new TypeViolation("group", 10,
//				"categoryValueRef", "participant", null, null);
//		assertEquals(expectedError, foundError);
//		assertTrue(results.get(1).isValid());
//	}
	
	/**
	 * Test method for
	 * {@link de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl#validate(java.lang.String)}
	 * with T19.
	 * 
	 * @throws ValidatorException
	 * 
	 */
	@Test
	public void testValidateWithT19() throws ValidatorException {
		ValidationResult result = application
				.validate("src/test/resources/test-19-referenz-6-korrekt.bpmn");
		assertEquals(2, result.getCheckedFiles().size());
		assertTrue(result.isValid());
	}
	
	private static void validateResultType(Violation foundError, int expectedLine, String validType, String typeToBeFound) {
		if(foundError.getConstraint()==null || !foundError.getConstraint().equals(BPMNReferenceValidatorImpl.CONSTRAINT_REF_TYPE)) {
			fail("found violation has the wrong type. Expected: "+BPMNReferenceValidatorImpl.CONSTRAINT_REF_TYPE+" Found: "+foundError.getConstraint());
		}
		
		if(foundError.getLine()<=0 || foundError.getLine()!=expectedLine) {
			fail("Violation is at an unexpected line. Expected: "+expectedLine+" Found: "+foundError.getLine());
		}
		
		if(foundError.getMessage()==null || !foundError.getMessage().contains("incorrect type "+typeToBeFound)) {
			fail("Violation Message does not contain expected String: 'incorrect type "+typeToBeFound+"'\n\t Message was: "+foundError.getMessage());
		} else if(!foundError.getMessage().contains(validType));
	}
	
	private static void assertViolationCount(ValidationResult result, int expectedViolationsCount) {
		if(expectedViolationsCount!=0) { 
			assertFalse(result.isValid());
			assertEquals(expectedViolationsCount, result.getViolations().size());
		} else {
			
		}
	}
}
