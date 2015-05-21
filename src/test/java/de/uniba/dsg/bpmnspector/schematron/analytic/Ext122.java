package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.122
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext122 extends TestCase {

    private static final String ERR_MSG = "A intermediateCatchEvent in normal flow must not contain a compensateEventDefinition.";


    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT122_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateCatchEvent[bpmn:compensateEventDefinition][0]", 7);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT122_success.bpmn"));
    }


    @Override
    protected String getExtNumber() {
        return "122";
    }
}
