package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.111
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext111 extends TestCase {

    private static final String ERR_MSG = "A cancel boundary event must be attached to a Transaction.";


    @Test
    public void testConstraintFailCancelSubProcess() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT111_failure_cancel_subprocess.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:boundaryEvent[bpmn:cancelEventDefinition][0]", 16);
    }

    @Test
    public void testConstraintFailCancelTask() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT111_failure_cancel_task.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:boundaryEvent[bpmn:cancelEventDefinition][0]", 16);
    }


    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT111_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "111";
    }
}
