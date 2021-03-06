package de.uniba.dsg.bpmnspector.schematron;

import api.ValidationException;
import api.ValidationResult;
import api.Violation;
import org.junit.Test;

/**
 * Test class for testing the preprocessing step to be able to detect
 * file-across errors
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class PreProcessing extends TestCase {

    private final static String ERRORMESSAGE = "Referenced process must have at least one None Start Event";
    private final static String XPATHSTRING = "//bpmn:*[@id = 'PROCESS_1']";

    @Test
    public void testConstraintImportedProcessFail()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_call_ref_process.bpmn"), 1);
        assertViolation(result.getViolations().get(0), "ref_process.bpmn", 3);
    }

    @Test
    public void testConstraintImportedProcessFail1()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_call_ref_process_call.bpmn"), 2);
        assertViolation(result.getViolations().get(0),
                "fail_call_ref_process.bpmn", 4);
        assertViolation(result.getViolations().get(1), "ref_process.bpmn", 3);
    }

    @Test
    public void testConstraintImportedProcessFail2()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_call_ref_process_call_call.bpmn"), 2);
        assertViolation(result.getViolations().get(0),
                "fail_call_ref_process.bpmn", 4);
        assertViolation(result.getViolations().get(1), "ref_process.bpmn", 3);
    }

    @Test
    public void testConstraintParticipantImportedProcessFail()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_call_participant_process.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "An end event must be present when a start event is used in the same process level",
                "//bpmn:*[@id = '_3']", 4);
    }

    @Test
    public void testTargetNamespaceIdReplacements()
            throws ValidationException {
        verifyValidResult(createFile("testTnsIdReplacement.bpmn"));
    }

    @Override
    protected void assertViolation(Violation v, String fileName, int line) {
        assertViolation(v, ERRORMESSAGE, fileName, XPATHSTRING, line);
    }

    @Override
    protected String getExtNumber() {
        return "preprocessing";
    }

}
