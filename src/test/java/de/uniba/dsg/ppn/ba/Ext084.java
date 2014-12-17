package de.uniba.dsg.ppn.ba;

import org.junit.Test;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

/**
 * Test class for testing Constraint EXT.084
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext084 extends TestCase {

    @Test
    public void testConstraintFail() throws BpmnValidationException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "A DataInput must be referenced by at least one InputSet",
                "//bpmn:dataInput[0]", 5);
    }

    @Test
    public void testConstraintSuccess() throws BpmnValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "084";
    }
}
