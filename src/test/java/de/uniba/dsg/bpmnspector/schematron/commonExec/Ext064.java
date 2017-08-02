package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.064
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext064 extends TestCase {

    private static final String ERR_MSG = "At least one InputOutputBinding must be defined as the Callable Element is exposed as a Service.";

    @Test
    public void testConstraintFailCalledProcess() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT064_failure_calledProcess.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:*[bpmn:supportedInterfaceRef])[1]", 21);
    }

    @Test
    public void testConstraintFailCalledGlobalTask() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT064_failure_calledGlobalTask.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:*[bpmn:supportedInterfaceRef])[1]", 54);
    }
    
    @Test
    public void testConstraintSuccessCalledProcess() throws ValidationException {
        verifyValidResult(createFile("EXT064_success_calledProcess.bpmn"));
    }

    @Test
    public void testConstraintSuccessCalledGlobalTask() throws ValidationException {
        verifyValidResult(createFile("EXT064_success_calledGlobalTask.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "064";
    }
}
