package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.049
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext049 extends TestCase {

    private static final String ERR_MSG = "A ReceiveTask with attribute instantiate set to true must not have any incoming sequence flow.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT049_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:receiveTask[@instantiate='true'][0]", 4);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT049_success.bpmn"));
    }

    @Test
    public void testConstraintSuccessInstantiateFalse() throws ValidationException {
        verifyValidResult(createFile("EXT049_success_instantiateFalse.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "049";
    }
}
