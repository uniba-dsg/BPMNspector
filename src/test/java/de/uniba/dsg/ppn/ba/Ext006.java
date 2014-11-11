package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
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
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertGeneralViolation(v, fileName);
    }

    private void assertSecondViolation(Violation v, String fileName, int line) {
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals(fileName, v.getFileName());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][0]",
                v.getxPath());
        assertEquals(line, v.getLine());
    }

    private void assertThirdViolation(Violation v, String fileName) {
        assertEquals(ERRORMESSAGETHREE, v.getMessage());
        assertGeneralViolation(v, fileName);
    }

    private void assertGeneralViolation(Violation v, String fileName) {
        assertEquals(fileName, v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "006";
    }
}
