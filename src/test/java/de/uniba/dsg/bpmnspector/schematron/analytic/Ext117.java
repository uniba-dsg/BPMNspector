package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.117
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext117 extends TestCase {

    private static final String ERR_MSG = "A Link Intermediate Event MUST NOT be both a target and a source of a Sequence Flow.";


    @Test
    public void testConstraintFailThrowOutgoing() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT117_failure_throwOutgoing.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateThrowEvent[bpmn:linkEventDefinition and bpmn:outgoing][0]", 11);
    }

    @Test
    public void testConstraintFailCatchIncoming() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT117_failure_CatchIncoming.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:intermediateCatchEvent[bpmn:linkEventDefinition and bpmn:incoming][0]", 19);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT117_success.bpmn"));
    }


    @Override
    protected String getExtNumber() {
        return "117";
    }
}
