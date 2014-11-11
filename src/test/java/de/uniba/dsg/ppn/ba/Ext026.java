package de.uniba.dsg.ppn.ba;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class Ext026 {

    private SchematronBPMNValidator validator;

    @Before
    public void setUp() {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }
	
    @Test
    public void testConstraintActivityFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "026" + File.separator
                + "fail_activity.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef",
                v.getMessage());
        assertEquals("//bpmn:task[@default][0]", v.getxPath());
        assertEquals(11, v.getLine());
    }

    @Test
    public void testConstraintGatewayFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "026" + File.separator
                + "fail_gateway.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals(
                "If an activity or gateway references a sequenceFlow as default flow - the referenced sequence flow must reference the activity/the gateway as sourceRef",
                v.getMessage());
        assertEquals("//bpmn:exclusiveGateway[@default][0]", v.getxPath());
        assertEquals(11, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "026" + File.separator
                + "success.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }
}
