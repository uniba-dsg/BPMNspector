package de.uniba.dsg.ppn.ba;

import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext006 extends TestCase {

    private final static String ERRORMESSAGEONE = "An Artifact MUST NOT be a target for a Sequence Flow";
    private final static String ERRORMESSAGETWO = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target";
    private final static String ERRORMESSAGETHREE = "The target element of the sequence flow must reference the SequenceFlow definition using their incoming attribute.";
    private final static String XPATHSTRING = "//bpmn:sequenceFlow[@targetRef][0]";

    @Test
    public void testConstraintAssociationFail() throws BpmnValidationException {
        String fileName = "Fail_association.bpmn";
        ValidationResult result = verifyInValidResult(createFile(fileName), 3);
        assertFirstViolation(result.getViolations().get(0), fileName);
        assertSecondViolation(result.getViolations().get(1), fileName, 11);
        assertThirdViolation(result.getViolations().get(2), fileName);
    }

    @Test
    public void testConstraintGroupFail() throws BpmnValidationException {
        String fileName = "Fail_group.bpmn";
        ValidationResult result = verifyInValidResult(createFile(fileName), 3);
        assertFirstViolation(result.getViolations().get(0), fileName);
        assertSecondViolation(result.getViolations().get(1), fileName, 8);
        assertThirdViolation(result.getViolations().get(2), fileName);
    }

    @Test
    public void testConstraintTextAnnotationFail()
            throws BpmnValidationException {
        String fileName = "Fail_text_annotation.bpmn";
        ValidationResult result = verifyInValidResult(createFile(fileName), 3);
        assertFirstViolation(result.getViolations().get(0), fileName);
        assertSecondViolation(result.getViolations().get(1), fileName, 8);
        assertThirdViolation(result.getViolations().get(2), fileName);
    }

    private void assertFirstViolation(Violation v, String fileName) {
        assertViolation(v, ERRORMESSAGEONE, fileName, XPATHSTRING, 7);
    }

    private void assertSecondViolation(Violation v, String fileName, int line) {
        assertViolation(v, ERRORMESSAGETWO, fileName,
                "//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][0]", line);
    }

    private void assertThirdViolation(Violation v, String fileName) {
        assertViolation(v, ERRORMESSAGETHREE, fileName, XPATHSTRING, 7);
    }

    @Override
    protected String getExtNumber() {
        return "006";
    }
}
