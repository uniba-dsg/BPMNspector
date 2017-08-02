package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.108
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext108 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("Fail.bpmn"),
                2);
        assertViolation(
                result.getViolations().get(0),
                "A message flow must connect 'InteractionNodes' from different Pools",
                "(//bpmn:messageFlow)[1]", 7);
        assertViolation(result.getViolations().get(1),
                "An End Event MUST NOT be a target for a message flow",
                "(//bpmn:messageFlow[@targetRef])[1]", 7);
    }

    @Override
    protected String getExtNumber() {
        return "108";
    }

}
