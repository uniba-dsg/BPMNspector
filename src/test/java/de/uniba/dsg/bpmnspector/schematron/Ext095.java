package de.uniba.dsg.bpmnspector.schematron;

import api.ValidationResult;
import api.ValidationException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.095
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext095 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:intermediateThrowEvent/bpmn:messageEventDefinition[0]",
                13);
    }

    @Test
    public void testConstraintEndFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_end.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:endEvent/bpmn:messageEventDefinition[0]", 10);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("Success.bpmn"));
    }

    @Override
    protected String getErrorMessage() {
        return "EventDefinitions defined in a throw event are not allowed to be used somewhere else";
    }

    @Override
    protected String getExtNumber() {
        return "095";
    }
}
