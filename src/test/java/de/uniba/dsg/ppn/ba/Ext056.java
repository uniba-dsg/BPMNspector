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

public class Ext056 {

    private SchematronBPMNValidator validator;
    private final static String ERRORMESSAGEONE = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target";
    private final static String ERRORMESSAGETWO = "A SubProcess must not contain Choreography Activities";
    private final static String ERRORMESSAGETHREE = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source";
    private final static String XPATHSTRINGONE = "//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][1]";
    private final static String XPATHSTRINGTWO = "//bpmn:*[./@id = //bpmn:sequenceFlow/@sourceRef][1]";

    @Before
    public void setUp() {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    @Test
    public void testConstraintCallChoreographyFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "056" + File.separator
                + "fail_call_choreography.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGETHREE, v.getMessage());
        assertEquals(XPATHSTRINGTWO, v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertEquals(XPATHSTRINGONE, v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(2);
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals("//bpmn:subProcess[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }

    @Test
    public void testConstraintChoreographyTaskFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "056" + File.separator
                + "fail_choreography_task.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGETHREE, v.getMessage());
        assertEquals(XPATHSTRINGTWO, v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertEquals(XPATHSTRINGONE, v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(2);
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals("//bpmn:subProcess[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }

    @Test
    public void testConstraintChoreographyTaskTransactionFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "056" + File.separator
                + "fail_choreography_task_transaction.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGETHREE, v.getMessage());
        assertEquals(XPATHSTRINGTWO, v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertEquals(XPATHSTRINGONE, v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(2);
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals("//bpmn:transaction[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }

    @Test
    public void testConstraintSubChoreographyFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "056" + File.separator
                + "fail_sub_choreography.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(3, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGETHREE, v.getMessage());
        assertEquals(XPATHSTRINGTWO, v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGEONE, v.getMessage());
        assertEquals(XPATHSTRINGONE, v.getxPath());
        assertEquals(11, v.getLine());
        v = result.getViolations().get(2);
        assertEquals(ERRORMESSAGETWO, v.getMessage());
        assertEquals("//bpmn:subProcess[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }
}
