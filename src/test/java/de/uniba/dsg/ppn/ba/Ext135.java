package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext135 extends TestCase {

    private final static String ERRORMESSAGE = "A Gateway MUST have either multiple incoming Sequence Flows or multiple outgoing Sequence Flows";

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(createFile("fail.bpmn"),
                2);
        assertViolation(result.getViolations().get(0),
                "//bpmn:parallelGateway[0]", 10);
        assertViolation(result.getViolations().get(1),
                "//bpmn:parallelGateway[1]", 20);
    }

    @Test
    public void testConstraintSubFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_no_connection.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:parallelGateway[0]", 4);
    }

    @Test
    public void testConstraintEXSubFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_ex_no_connection.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:exclusiveGateway[0]", 4);
    }

    @Test
    public void testConstraintBothMultipleSuccess()
            throws BpmnValidationException {
        verifyValidResult(createFile("success_multiple_in_and_out.bpmn"));
    }

    @Test
    public void testConstraintOutMultipleSuccess()
            throws BpmnValidationException {
        verifyValidResult(createFile("success_multiple_out.bpmn"));
    }

    private void assertViolation(Violation v, String xpath, int line) {
        assertEquals(ERRORMESSAGE, v.getMessage());
        assertEquals(xpath, v.getxPath());
        assertEquals(line, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "135";
    }
}
