package de.uniba.dsg.ppn.ba;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext104 extends TestCase {

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(createFile("fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "An End Event must not have an outgoing sequence flow",
                "//bpmn:endEvent[0]", 7);
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "104";
    }
}
