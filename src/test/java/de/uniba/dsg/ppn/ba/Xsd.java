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

public class Xsd {

    private SchematronBPMNValidator validator;

    @Before
    public void setUp() {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    @Test
    public void testXsdFail() throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "xsd" + File.separator
                + "xsdfail.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertEquals("xsdfail.bpmn", v.getFileName());
        assertEquals(6, v.getLine());
        assertTrue(v.getMessage().contains("cvc-complex-type.2.4.a:"));
        assertTrue(v.getMessage().contains("outgoing"));
        assertEquals("", v.getxPath());
        assertEquals("XSD-Check", v.getConstraint());
    }
}
