package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

/**
 * Test class for testing the XSD validation
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Xsd extends TestCase {

    @Test
    public void testXsdFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("xsdfail.bpmn"), 1);
        Violation v = result.getViolations().get(0);
        assertEquals("xsdfail.bpmn", v.getFileName());
        assertEquals(6, v.getLine());
        assertTrue(v.getMessage().contains("cvc-complex-type.2.4.a:"));
        assertTrue(v.getMessage().contains("outgoing"));
        assertEquals("", v.getxPath());
        assertEquals("XSD-Check", v.getConstraint());
    }

    @Override
    protected String getExtNumber() {
        return "xsd";
    }
}
