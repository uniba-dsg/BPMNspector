package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext036 extends TestCase {

    private static final String ERRORMESSAGESOURCE = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source";
    private static final String ERRORMESSAGETARGET = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target";

    @Test
    public void testConstraintCallChoreographyFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_call_choreography.bpmn"), 3);
        assertFirstViolation(result.getViolations().get(0));
        assertSecondViolation(result.getViolations().get(1));
        assertThirdViolation(result.getViolations().get(2));
    }

    @Test
    public void testConstraintChoreographyTaskFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_choreography_task.bpmn"), 3);
        assertFirstViolation(result.getViolations().get(0));
        assertSecondViolation(result.getViolations().get(1));
        assertThirdViolation(result.getViolations().get(2));
    }

    @Test
    public void testConstraintSubChoreographyFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_sub_choreography.bpmn"), 3);
        assertFirstViolation(result.getViolations().get(0));
        assertSecondViolation(result.getViolations().get(1));
        assertThirdViolation(result.getViolations().get(2));
    }

    private void assertFirstViolation(Violation v) {
        assertEquals(ERRORMESSAGESOURCE, v.getMessage());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@sourceRef][1]",
                v.getxPath());
        assertEquals(10, v.getLine());
    }

    private void assertSecondViolation(Violation v) {
        assertEquals(ERRORMESSAGETARGET, v.getMessage());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][1]",
                v.getxPath());
        assertEquals(10, v.getLine());
    }

    private void assertThirdViolation(Violation v) {
        assertEquals("A Process must not contain Choreography Activities",
                v.getMessage());
        assertEquals("//bpmn:process[0]", v.getxPath());
        assertEquals(3, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "036";
    }

}
