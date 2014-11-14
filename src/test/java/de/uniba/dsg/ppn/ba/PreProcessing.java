package de.uniba.dsg.ppn.ba;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class PreProcessing extends TestCase {

    private final static String ERRORMESSAGE = "Referenced process must have at least one None Start Event";
    private final static String XPATHSTRING = "//bpmn:*[@id = 'PROCESS_1'][0]";

    @Test
    public void testConstraintImportedProcessFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_call_ref_process.bpmn"), 1);
        assertViolation(result.getViolations().get(0), "ref_process.bpmn", 3);
    }

    @Test
    public void testConstraintImportedProcessFail1()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_call_ref_process_call.bpmn"), 2);
        assertViolation(result.getViolations().get(0),
                "fail_call_ref_process.bpmn", 4);
        assertViolation(result.getViolations().get(1), "ref_process.bpmn", 3);
    }

    @Test
    public void testConstraintImportedProcessFail2()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_call_ref_process_call_call.bpmn"), 2);
        assertViolation(result.getViolations().get(0),
                "fail_call_ref_process.bpmn", 4);
        assertViolation(result.getViolations().get(1), "ref_process.bpmn", 3);
    }

    @Test
    public void testConstraintParticipantImportedProcessFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_call_participant_process.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "An end event must be present when a start event is used in the same process level",
                "//bpmn:*[@id = '_3'][0]", 4);
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
