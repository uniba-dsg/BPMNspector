package de.uniba.dsg.ppn.ba;

import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

/**
 * Test class for testing Constraint EXT.103
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext103 extends TestCase {

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(
                result.getViolations().get(0),
                "If a Start Event is target of a MessageFlow definition, at least one messageEventDefinition must be present",
                "//bpmn:startEvent[@id = //bpmn:messageFlow/@targetRef][0]", 13);
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
