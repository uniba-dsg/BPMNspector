package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext103 extends TestCase {

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                1);
        Violation v = result.getViolations().get(0);
        assertEquals(
                "If a Start Event is target of a MessageFlow definition, at least one messageEventDefinition must be present",
                v.getMessage());
        assertEquals(
                "//bpmn:startEvent[@id = //bpmn:messageFlow/@targetRef][0]",
                v.getxPath());
        assertEquals(13, v.getLine());
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("Success.bpmn"));
    }

    @Test
    public void testConstraintRefSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("Success_ref.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "103";
    }
}
