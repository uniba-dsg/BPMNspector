package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.143
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext143 extends TestCase {

    private static final String ERR_MSG = "A compensateBoundaryEvent must be connected with an Association to a Compensation Activity.";


    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT143_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:boundaryEvent[bpmn:compensateEventDefinition][0]", 5);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT143_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "143";
    }
}
