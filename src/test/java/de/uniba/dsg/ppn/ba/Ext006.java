package de.uniba.dsg.ppn.ba;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Ext006 {

    private SchematronBPMNValidator validator;
    private final static String ERRORMESSAGEONE = "An Artifact MUST NOT be a target for a Sequence Flow";
    private final static String ERRORMESSAGETWO = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target";
    private final static String ERRORMESSAGETHREE = "The target element of the sequence flow must reference the SequenceFlow definition using their incoming attribute.";
    private final static String XPATHSTRING = "//bpmn:sequenceFlow[@targetRef][0]";

    @Before
    public void setUp() {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    @Test
    public void testConstraintAssociationFail() throws BpmnValidationException {
        String fileName = "Fail_association.bpmn";
        File f = new File(TestHelper.getTestFilePath() + "006" + File.separator
                + fileName);
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertEquals(fileName, v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(7, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals(fileName, v.getFileName());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][0]",
                v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(2);
        assertEquals(ERRORMESSAGETHREE, v.getMessage());
        assertEquals(fileName, v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintGroupFail() throws BpmnValidationException {
        String fileName = "Fail_group.bpmn";
        File f = new File(TestHelper.getTestFilePath() + "006" + File.separator
                + fileName);
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertEquals(fileName, v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(7, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals(fileName, v.getFileName());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][0]",
                v.getxPath());
        assertEquals(8, v.getLine());
        v = result.getViolations().get(2);
        assertEquals(ERRORMESSAGETHREE, v.getMessage());
        assertEquals(fileName, v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintTextAnnotationFail()
            throws BpmnValidationException {
        String fileName = "Fail_text_annotation.bpmn";
        File f = new File(TestHelper.getTestFilePath() + "006" + File.separator
                + fileName);
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertEquals(fileName, v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(7, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals(fileName, v.getFileName());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][0]",
                v.getxPath());
        assertEquals(8, v.getLine());
        v = result.getViolations().get(2);
        assertEquals(ERRORMESSAGETHREE, v.getMessage());
        assertEquals(fileName, v.getFileName());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals(7, v.getLine());
    }
}
