package de.uniba.dsg.ppn.ba;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext076 extends TestCase {

    @Test
    public void testConstraintFail1() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_1.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintFail2() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_2.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintFail3() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_3.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("Success.bpmn"));
    }

    private void assertViolation(Violation v) {
        assertViolation(v, "//bpmn:dataObjectReference[@name][0]", 5);
    }

    @Override
    protected String getErrorMessage() {
        return "Naming Convention: name = Data Object Name [Data Object Reference State]";
    }

    @Override
    protected String getExtNumber() {
        return "076";
    }
}
