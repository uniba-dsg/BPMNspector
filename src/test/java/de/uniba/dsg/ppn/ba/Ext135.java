package de.uniba.dsg.ppn.ba;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.135
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext135 extends TestCase {

    private final static String ERRORMESSAGE = "A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows";

    @Test
    public void testConstraintFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(createFile("fail.bpmn"),
                2);
        assertViolation(result.getViolations().get(0),
                "//bpmn:parallelGateway[0]", 10);
        assertViolation(result.getViolations().get(1),
                "//bpmn:parallelGateway[1]", 20);
    }

    @Test
    public void testConstraintSubFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_no_connection.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:parallelGateway[0]", 4);
    }

    @Test
    public void testConstraintEXSubFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_ex_no_connection.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:exclusiveGateway[0]", 4);
    }

    @Test
    public void testConstraintBothMultipleSuccess()
            throws ValidatorException {
        verifyValidResult(createFile("success_multiple_in_and_out.bpmn"));
    }

    @Test
    public void testConstraintOutMultipleSuccess()
            throws ValidatorException {
        verifyValidResult(createFile("success_multiple_out.bpmn"));
    }

    @Override
    protected String getErrorMessage() {
        return ERRORMESSAGE;
    }

    @Override
    protected String getExtNumber() {
        return "135";
    }
}
