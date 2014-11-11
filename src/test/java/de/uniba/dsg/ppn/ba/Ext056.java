package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext056 extends TestCase {

    private final static String ERRORMESSAGETARGET = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target";
    private final static String ERRORMESSAGECOREGRAPHY = "A SubProcess must not contain Choreography Activities";
    private final static String ERRORMESSAGESOURCE = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source";
    private final static String XPATHSTRINGTARGET = "//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][1]";
    private final static String XPATHSTRINGSOURCE = "//bpmn:*[./@id = //bpmn:sequenceFlow/@sourceRef][1]";

    @Test
    public void testConstraintCallChoreographyFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_call_choreography.bpmn"), 3);
        assertFirstViolation(result.getViolations().get(0));
        assertSourceViolation(result.getViolations().get(1));
        assertTargetViolation(result.getViolations().get(2),
                "//bpmn:subProcess[0]");
    }

    @Test
    public void testConstraintChoreographyTaskFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_choreography_task.bpmn"), 3);
        assertFirstViolation(result.getViolations().get(0));
        assertSourceViolation(result.getViolations().get(1));
        assertTargetViolation(result.getViolations().get(2),
                "//bpmn:subProcess[0]");
    }

    @Test
    public void testConstraintChoreographyTaskTransactionFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_choreography_task_transaction.bpmn"), 3);
        assertFirstViolation(result.getViolations().get(0));
        assertSourceViolation(result.getViolations().get(1));
        assertTargetViolation(result.getViolations().get(2),
                "//bpmn:transaction[0]");
    }

    @Test
    public void testConstraintSubChoreographyFail()
            throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_sub_choreography.bpmn"), 3);
        assertFirstViolation(result.getViolations().get(0));
        assertSourceViolation(result.getViolations().get(1));
        assertTargetViolation(result.getViolations().get(2),
                "//bpmn:subProcess[0]");
    }

    private void assertFirstViolation(Violation v) {
        assertEquals(ERRORMESSAGESOURCE, v.getMessage());
        assertEquals(XPATHSTRINGSOURCE, v.getxPath());
        assertEquals(11, v.getLine());
    }

    private void assertSourceViolation(Violation v) {
        assertEquals(ERRORMESSAGETARGET, v.getMessage());
        assertEquals(XPATHSTRINGTARGET, v.getxPath());
        assertEquals(11, v.getLine());
    }

    private void assertTargetViolation(Violation v, String xpath) {
        assertEquals(ERRORMESSAGECOREGRAPHY, v.getMessage());
        assertEquals(xpath, v.getxPath());
        assertEquals(4, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "056";
    }
}
