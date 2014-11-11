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

public class Wsdl {

    private SchematronBPMNValidator validator;

    @Before
    public void setUp() {
        validator = new SchematronBPMNValidator();
        validator.setLogLevel(Level.OFF);
    }

    @Test
    public void testConstraintImportedWsdlSuccess()
            throws BpmnValidationException {
        File f = new File(TestHelper.getTestFilePath() + "wsdl"
                + File.separator + "wsdl-success.bpmn");
        ValidationResult result = validator.validate(f);
        assertTrue(result.isValid());
        assertTrue(result.getViolations().isEmpty());
    }

    @Test
    public void testConstraintImportedWsdlFail() throws BpmnValidationException {
        String fileName = "wsdl2primer-fail.wsdl";
        File f = new File(TestHelper.getTestFilePath() + "wsdl"
                + File.separator + "wsdl-fail.bpmn");
        ValidationResult result = validator.validate(f);
        assertFalse(result.isValid());
        assertEquals(4, result.getViolations().size());
        Violation v = result.getViolations().get(0);
        assertTrue(v.getMessage().contains("cvc-complex-type.2.4.c:"));
        assertTrue(v.getMessage().contains("xs:schema"));
        assertEquals("", v.getxPath());
        assertEquals(fileName, v.getFileName());
        assertEquals(22, v.getLine());
        v = result.getViolations().get(1);
        assertTrue(v.getMessage().contains("cvc-complex-type.2.4.a:"));
        assertTrue(v.getMessage().contains("DAMAGEDinterface"));
        assertEquals("", v.getxPath());
        assertEquals(fileName, v.getFileName());
        assertEquals(40, v.getLine());
        v = result.getViolations().get(2);
        assertTrue(v.getMessage().contains("cvc-complex-type.3.2.2:"));
        assertTrue(v.getMessage().contains("DAMinterface"));
        assertTrue(v.getMessage().contains("service"));
        assertEquals("", v.getxPath());
        assertEquals(fileName, v.getFileName());
        assertEquals(72, v.getLine());
        v = result.getViolations().get(3);
        assertTrue(v.getMessage().contains("cvc-complex-type.4:"));
        assertTrue(v.getMessage().contains("interface"));
        assertTrue(v.getMessage().contains("service"));
        assertEquals("", v.getxPath());
        assertEquals(fileName, v.getFileName());
        assertEquals(72, v.getLine());
    }
}
