package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.141
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext141 extends TestCase {

    private static final String ERR_MSG = "Targets of an EventBasedGateway must not have any other incoming SequenceFlow.";


    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT141_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "(//bpmn:*[@id=//bpmn:sequenceFlow[@id=//bpmn:eventBasedGateway/bpmn:outgoing]/@targetRef])[2]", 25);
    }

    @Override
    protected String getExtNumber() {
        return "141";
    }
}
