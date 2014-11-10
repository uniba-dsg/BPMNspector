package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class Ext022 {

    private SchematronBPMNValidator validator;

    @Before
    public void setUp() {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    @After
    public void tearDown() {
        validator = null;
    }

    @Test
    public void testConstraintEventSubProcessFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "022" + File.separator
                + "fail_event_sub_process.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target",
                v.getMessage());
        assertEquals("//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][0]",
                v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintEventsSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "022" + File.separator
                + "success_events.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintGatewaysSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "022" + File.separator
                + "success_gateways.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintTasksSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "022" + File.separator
                + "success_tasks.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintTasksSuccess_EventProcess_In_SubProcess()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "022" + File.separator
                + "success_event_in_normal_subprocess.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
