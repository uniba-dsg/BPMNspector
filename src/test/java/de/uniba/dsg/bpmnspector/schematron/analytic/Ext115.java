package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.115
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext115 extends TestCase {

    private static final String ERR_MSG = "Intermediate Events MUST be a target of at least a Sequence Flow.";


    @Test
    public void testConstraintFailNoIncoming() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT115_failure_noIncoming.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateThrowEvent[not(bpmn:linkEventDefinition) and not(bpmn:incoming)][0]", 11);
    }

    @Test
    public void testConstraintFailNoIncomingCatch() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT115_failure_noIncomingCatch.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateCatchEvent[not(bpmn:linkEventDefinition) and not(bpmn:incoming)][0]", 11);
    }

    @Test
         public void testConstraintSuccess_noStartEnd() throws ValidationException {
        verifyValidResult(createFile("EXT115_success_noStartEnd.bpmn"));
    }

    @Test
    public void testConstraintSuccess_StartEnd() throws ValidationException {
        verifyValidResult(createFile("EXT115_success_StartEnd.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "115";
    }
}
