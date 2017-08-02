package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.017
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext017 extends TestCase {

    private static final String ERR_MSG = "A converging Gateway must not have more than one outgoing Sequence Flow.";

    @Test
    public void testConstraintFailExclusiveGateway() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT017_failure_exclusiveGateway.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:*[@gatewayDirection='Converging'])[1]", 7);
    }

    @Test
    public void testConstraintSuccessComplexGateway() throws ValidationException {
        verifyValidResult(createFile("EXT017_success_complexGateway.bpmn"));
    }

    @Test
    public void testConstraintSuccessExclusiveGateway() throws ValidationException {
        verifyValidResult(createFile("EXT017_success_exclusiveGateway.bpmn"));
    }

    @Test
    public void testConstraintSuccessInclusiveGateway() throws ValidationException {
        verifyValidResult(createFile("EXT017_success_inclusiveGateway.bpmn"));
    }

    @Test
    public void testConstraintSuccessParallelGateway() throws ValidationException {
        verifyValidResult(createFile("EXT017_success_parallelGateway.bpmn"));
    }


    @Override
    protected String getExtNumber() {
        return "017";
    }
}
