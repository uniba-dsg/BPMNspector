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

public class Ext008 {

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
    public void testConstraintAssociationFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "008" + File.separator
                + "Fail_association.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals("An Artifact MUST NOT be a target for a Message Flow",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[@targetRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintGroupFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "008" + File.separator
                + "Fail_group.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals("An Artifact MUST NOT be a target for a Message Flow",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[@targetRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintTextAnnotationFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "008" + File.separator
                + "Fail_text_annotation.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals("An Artifact MUST NOT be a target for a Message Flow",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[@targetRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

}
