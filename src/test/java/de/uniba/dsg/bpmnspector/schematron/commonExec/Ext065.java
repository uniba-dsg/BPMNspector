package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.065
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext065 extends TestCase {

    private static final String ERR_MSG = "An InputOutputBinding element must correctly bind one Input and one Output of" +
            " the InputOutputSpecification to an Operation of a Service Interface.";

    @Test
    public void testConstraintFailCalledProcess() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT065_failure_calledProcess_wrongInputData.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:*[bpmn:supportedInterfaceRef and bpmn:ioBinding])[1]", 19);
    }

    @Test
    public void testConstraintFailCalledGlobalTask() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT065_failure_calledGlobalTask_wrongOutputData.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:*[bpmn:supportedInterfaceRef and bpmn:ioBinding])[1]", 14);
    }


    @Override
    protected String getExtNumber() {
        return "065";
    }
}
