package de.uniba.dsg.bpmnspector.schematron;

import api.ValidationResult;
import api.ValidationException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.109
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext109 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(
                result.getViolations().get(0),
                "If an end event is source of a MessageFlow definition, at least one messageEventDefinition must be present",
                "//bpmn:endEvent[@id = //bpmn:messageFlow/@sourceRef][0]", 16);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("Success.bpmn"));
    }

    @Test
    public void testConstraintRefSuccess() throws ValidationException {
        verifyValidResult(createFile("Success_ref.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "109";
    }
}
