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

public class Ext009 {

    private SchematronBPMNValidator validator;

    @Before
    public void setUp() {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    @Test
    public void testConstraintAssociationFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "009" + File.separator
                + "Fail_association.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals("An Artifact MUST NOT be a source for a Message Flow",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[@sourceRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintGroupFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "009" + File.separator
                + "Fail_group.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals("An Artifact MUST NOT be a source for a Message Flow",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[@sourceRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }

    @Test
    public void testConstraintTextAnnotationFail()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "009" + File.separator
                + "Fail_text_annotation.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals("An Artifact MUST NOT be a source for a Message Flow",
                v.getMessage());
        assertEquals("//bpmn:messageFlow[@sourceRef][0]", v.getxPath());
        assertEquals(7, v.getLine());
    }
}
