package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.137
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext137 extends TestCase {

    private static final String ERR_MSG = "The outgoing Sequence Flows of the Event Gateway MUST NOT have a conditionExpression.";


    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT137_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "(//bpmn:sequenceFlow[@id=//bpmn:eventBasedGateway/bpmn:outgoing])[1]", 18);
    }

    @Override
    protected String getExtNumber() {
        return "137";
    }
}
