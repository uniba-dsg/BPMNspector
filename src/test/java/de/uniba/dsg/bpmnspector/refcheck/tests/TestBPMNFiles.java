package de.uniba.dsg.bpmnspector.refcheck.tests;

import api.UnsortedValidationResult;
import api.ValidationResult;
import api.ValidationException;
import api.Violation;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.importer.ProcessImporter;
import de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidator;
import de.uniba.dsg.bpmnspector.refcheck.ReferenceChecker;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

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
	private static ProcessImporter bpmnImporter;

	@BeforeClass
	public static void setupBeforeClass() throws ValidationException {
		application = new BPMNReferenceValidator();
		bpmnImporter = new ProcessImporter();
	}

	@Test
	public void testValidateWithT1() throws ValidationException {

		ValidationResult result = importAndTestProcess("src/test/resources/test-1-gruppe-c.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
				
		validateResultType(result.getViolations().get(0), 11, "message", "participant");
		
	}

	@Test
	public void testValidateWithT2() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-2-gruppe-d.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 3, "itemRef", "participant");
		
	}

	@Test
	public void testValidateWithT3() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-3-gruppe-e.bpmn");

		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 2);
		
		validateResultType(result.getViolations().get(0), 32, "sourceRef", "participant");
		validateResultType(result.getViolations().get(1), 33, "targetRef", "sequenceFlow");
		
	}

	@Test
	public void testValidateWithT4() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-4-gruppe-f.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 2);
		
		validateResultType(result.getViolations().get(0), 10, "incoming", "participant");
		validateResultType(result.getViolations().get(1), 30, "default", "startEvent");
	}

	@Test
	public void testValidateWithT5() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-5-gruppe-g.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 11, "interfaceRef", "task");
	}

	@Test
	public void testValidateWithT6() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-6-gruppe-h.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 23, "participantRef", "sequenceFlow");
	}

	@Test
	public void testValidateWithT7() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-7-gruppe-i1.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 2);
		
		validateResultType(result.getViolations().get(0), 11, "sourceRef", "message");
		validateResultType(result.getViolations().get(1), 11, "targetRef", "process");
	}

	@Test
	public void testValidateWithT8() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-8-gruppe-l.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 22, "operationRef", "message");
	}

	@Test
	public void testValidateWithT9() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-9-gruppe-m.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 18, "dataInputRefs", "participant");
	}

	@Test
	public void testValidateWithT10() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-10-gruppe-n.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 19, "dataOutputRefs", "participant");
	}

	@Test
	public void testValidateWithT11() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-11-gruppe-o.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 2);
		
		validateResultType(result.getViolations().get(0), 23, "sourceRef", "participant");
		validateResultType(result.getViolations().get(1), 44, "targetRef", "startEvent");
	}

	@Test
	public void testValidateWithT12() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-12-gruppe-p.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 27, "eventDefinitionRef", "sequenceFlow");
	}

	@Test
	public void testValidateWithT13() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-13-gruppe-q.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 19, "attachedToRef", "participant");
	}

	@Test
	public void testValidateWithT14() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-14-gruppe-r.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 2);
		
		validateResultType(result.getViolations().get(0), 24, "source", "startEvent");
		validateResultType(result.getViolations().get(1), 43, "target", "endEvent");
	}

	@Test
	public void testValidateWithT15() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-15-gruppe-s.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 18, "definitionalCollaborationRef", "message");
	}

	@Test
	public void testValidateWithT16() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-16-gruppe-t.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 23, "correlationPropertyRef", "participant");
	}

	@Test
	public void testValidateWithT17() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-17-gruppe-u.bpmn");
		
		assertEquals(1, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 8, "errorRef", "message");
	}

	@Test
	public void testValidateWithT18() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-18-referenz-6-teil-2.bpmn");
		
		assertEquals(2, result.getFoundFiles().size());
		
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 10, "categoryValueRef", "participant");
	}

	@Test
	public void testValidateWithT18Subfolder() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-18-referenz-6-teil-2-subfolder.bpmn");
		assertEquals(2, result.getFoundFiles().size());
				
		assertViolationCount(result, 1);
		
		validateResultType(result.getViolations().get(0), 10, "categoryValueRef", "participant");
	}

	@Test
	public void testValidateWithT19() throws ValidationException {
		ValidationResult result = importAndTestProcess("src/test/resources/test-19-referenz-6-korrekt.bpmn");
		assertEquals(2, result.getFoundFiles().size());
		assertTrue(result.isValid());
	}

	private ValidationResult importAndTestProcess(String filename) throws ValidationException {
		ValidationResult result = new UnsortedValidationResult();
		BPMNProcess process = bpmnImporter
				.importProcessFromPath(Paths.get(filename), result);

		application.validate(process, result);
		return result;
	}

	private static void validateResultType(Violation foundError, int expectedLine, String validType, String typeToBeFound) {
		if(foundError.getConstraint()==null || !foundError.getConstraint().equals(
				ReferenceChecker.CONSTRAINT_REF_TYPE)) {
			fail("found violation has the wrong type. Expected: "+ ReferenceChecker.CONSTRAINT_REF_TYPE+" Found: "+foundError.getConstraint());
		}
		
		if(foundError.getLocation().getLocation().getRow()<=0 || foundError.getLocation().getLocation().getRow()!=expectedLine) {
			fail("Violation is at an unexpected line. Expected: "+expectedLine+" Found: "+foundError.getLocation().getLocation().getRow());
		}
		
		if(foundError.getMessage()==null || !foundError.getMessage().contains("incorrect type "+typeToBeFound)) {
			fail("Violation Message does not contain expected String: 'incorrect type "+typeToBeFound+"'\n\t Message was: "+foundError.getMessage());
		} else if(!foundError.getMessage().contains(validType)) {
			fail("Valid Type ("+validType+") is not found in list of expected types."+foundError.getMessage());
		}
	}
	
	private static void assertViolationCount(ValidationResult result, int expectedViolationsCount) {
		if(expectedViolationsCount!=0) { 
			assertFalse(result.isValid());
			assertEquals(expectedViolationsCount, result.getViolations().size());
		}
	}
}
