package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class PreProcessing {

    private SchematronBPMNValidator validator = null;
    private final static String ERRORMESSAGE = "Referenced process must have at least one None Start Event";
    private final static String XPATHSTRING = "//bpmn:*[@id = 'PROCESS_1'][0]";

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
    public void testConstraintImportedProcessFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "preprocessing"
                + File.separator + "fail_call_ref_process.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals("ref_process.bpmn", v.getFileName());
        assertEquals(3, v.getLine());
    }

    @Test
    public void testConstraintImportedProcessFail1()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "preprocessing"
                + File.separator + "fail_call_ref_process_call.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals("fail_call_ref_process.bpmn", v.getFileName());
        assertEquals(4, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals("ref_process.bpmn", v.getFileName());
        assertEquals(3, v.getLine());
    }

    @Test
    public void testConstraintImportedProcessFail2()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "preprocessing"
                + File.separator + "fail_call_ref_process_call_call.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals("fail_call_ref_process.bpmn", v.getFileName());
        assertEquals(4, v.getLine());
        v = result.getViolations().get(1);
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(XPATHSTRING, v.getxPath());
        assertEquals("ref_process.bpmn", v.getFileName());
        assertEquals(3, v.getLine());
    }

    @Test
    public void testConstraintParticipantImportedProcessFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "preprocessing"
                + File.separator + "fail_call_participant_process.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "An end event must be present when a start event is used in the same process level",
                v.getMessage());
        assertEquals("//bpmn:*[@id = '_3'][0]", v.getxPath());
        assertEquals("ref_participant_process.bpmn", v.getFileName());
        assertEquals(4, v.getLine());
    }

}
