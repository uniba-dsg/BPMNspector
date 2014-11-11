package de.uniba.dsg.ppn.ba;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Ext096 extends TestCase {

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                1);
        Violation v = result.getViolations().get(0);
        assertEquals("A Start Event must not have an incoming sequence flow",
                v.getMessage());
        assertEquals("//bpmn:startEvent[0]", v.getxPath());
        assertEquals(4, v.getLine());
    }

    @Override
    protected String getExtNumber() {
        return "096";
    }
}
