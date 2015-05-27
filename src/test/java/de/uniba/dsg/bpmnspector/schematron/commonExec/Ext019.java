package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.019
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext019 extends TestCase {

    private static final String ERR_MSG = "A mixed Gateway must have more than one incoming and more than one outgoing Sequence Flow.";

    @Test
    public void testConstraintFailExclusiveDiverging() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT019_failure_exclusiveDiverging.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:*[@gatewayDirection='Mixed'][0]", 12);
    }

    @Test
    public void testConstraintFailParallelConverging() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT019_failure_parallelConverging.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:*[@gatewayDirection='Mixed'][0]", 18);
    }

    @Test
    public void testConstraintSuccessParallel() throws ValidationException {
        verifyValidResult(createFile("EXT019_success_parallel.bpmn"));
    }


    @Override
    protected String getExtNumber() {
        return "019";
    }
}
