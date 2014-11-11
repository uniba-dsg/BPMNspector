package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext095 extends TestCase {

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:intermediateThrowEvent/bpmn:messageEventDefinition[0]",
                13);
    }

    @Test
    public void testConstraintEndFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_end.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                "//bpmn:endEvent/bpmn:messageEventDefinition[0]", 10);
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("Success.bpmn"));
    }

    private void assertViolation(Violation v, String xpath, int line) {
        assertEquals(
                "EventDefinitions defined in a throw event are not allowed to be used somewhere else",
                v.getMessage());
        assertEquals(xpath, v.getxPath());
        assertEquals(line, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "095";
    }
}
