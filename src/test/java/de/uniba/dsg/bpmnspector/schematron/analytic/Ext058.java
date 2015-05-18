package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.058
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext058 extends TestCase {

    private static final String ERR_MSG = "An Event Sub-Process MUST have exactly one Start Event.";

    @Test
    public void testConstraintFailTwoStartEvents() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT058_fail_twoStartEvents.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:subProcess[@triggeredByEvent='true'][0]", 27);
    }

    @Test
    public void testConstraintFailNoStartEvent() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT058_fail_noStartEvent.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:subProcess[@triggeredByEvent='true'][0]", 27);
    }

    @Override
    protected String getExtNumber() {
        return "058";
    }
}
