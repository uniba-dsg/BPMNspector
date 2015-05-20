package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.112
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext112 extends TestCase {

    private static final String ERR_MSG = "A boundary event must not be target of a Sequence Flow.";


    @Test
    public void testConstraintFailIncomingSeqFlow() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT112_failure_incomingSeqFlow.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:boundaryEvent[bpmn:incoming][0]", 23);
    }


    @Override
    protected String getExtNumber() {
        return "112";
    }
}
