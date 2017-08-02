package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.018
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext018 extends TestCase {

    private static final String ERR_MSG = "A diverging Gateway must not have more than one incoming Sequence Flow.";

    @Test
    public void testConstraintFailExclusiveGateway() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT018_failure_exclusiveGateway.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:*[@gatewayDirection='Diverging'])[2]", 13);
    }

    @Test
    public void testConstraintFailParallelGateway() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT018_failure_parallelGateway.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:*[@gatewayDirection='Diverging'])[2]", 13);
    }


    @Override
    protected String getExtNumber() {
        return "018";
    }
}
