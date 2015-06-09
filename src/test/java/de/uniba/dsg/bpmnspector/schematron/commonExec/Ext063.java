package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.070
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext063 extends TestCase {

    private static final String ERR_MSG = "A Call Activity MUST fulfill the data requirements, as well as return the data " +
            "produced by the CallableElement being invoked. This means that the elements contained in the Call Activity's " +
            "InputOutputSpecification MUST exactly match the elements contained in the referenced CallableElement. " +
            "This includes DataInputs, DataOutputs, InputSets, and OutputSets.";

    @Test
    public void testConstraintFailCalledProcessAdditionalInput() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT063_failure_calledProcess_additionalInput.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:callActivity[@calledElement][0]", 62);
    }

    @Test
    public void testConstraintFailCalledProcessInputMissing() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT063_failure_calledProcess_inputMissing.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:callActivity[@calledElement][0]", 62);
    }

    @Test
    public void testConstraintFailCalledProcessOutputMissing() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT063_failure_calledProcess_outputMissing.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:callActivity[@calledElement][0]", 62);
    }

    @Test
    public void testConstraintFailCalledProcessItemsNotMatching() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT063_failure_calledProcess_itemsNotMatching.bpmn"), 2);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:outputSet[ancestor::bpmn:callActivity and node()][0]", 73);
        assertViolation(result.getViolations().get(1),
                ERR_MSG,
                "//bpmn:dataOutput[ancestor::bpmn:callActivity][0]", 69);
    }
    
    @Test
    public void testConstraintSuccessCalledProcess() throws ValidationException {
        verifyValidResult(createFile("EXT063_success_calledProcess.bpmn"));
    }

    @Test
    public void testConstraintSuccessCalledProcessDifferentItemsMatchingStructure() throws ValidationException {
        verifyValidResult(createFile("EXT063_success_calledProcess_differentItemsMatchingStructure.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "063";
    }
}
