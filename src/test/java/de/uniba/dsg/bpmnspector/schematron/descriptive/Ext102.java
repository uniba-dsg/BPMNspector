package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationResult;
import api.ValidationException;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.102
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext102 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("Fail.bpmn"),
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
