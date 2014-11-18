package de.uniba.dsg.ppn.ba;

import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext102 extends TestCase {

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                2);
        assertViolation(
                result.getViolations().get(0),
                "A message flow must connect ’InteractionNodes’ from different Pools",
                "//bpmn:messageFlow[0]", 7);
        assertViolation(result.getViolations().get(1),
                "A Start Event MUST NOT be a source for a message flow",
                "//bpmn:messageFlow[@sourceRef][0]", 7);
    }

    @Override
    protected String getExtNumber() {
        return "102";
    }
}
