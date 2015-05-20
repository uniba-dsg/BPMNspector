package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.113
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext113 extends TestCase {

    private static final String ERR_MSG = "A boundary event must be a source of at least a SequenceFlow.";


    @Test
    public void testConstraintFailNoSeqFlow() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT113_failure_noSeqFlow.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:boundaryEvent[not(bpmn:outgoing)][0]", 16);
    }


    @Override
    protected String getExtNumber() {
        return "113";
    }
}
