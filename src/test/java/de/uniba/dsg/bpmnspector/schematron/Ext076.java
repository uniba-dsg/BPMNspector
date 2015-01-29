package de.uniba.dsg.bpmnspector.schematron;

import api.ValidationResult;
import api.ValidationException;
import api.Violation;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.076
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext076 extends TestCase {

    @Test
    public void testConstraintFail1() throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_1.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintFail2() throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_2.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintFail3() throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("Fail_3.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
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
