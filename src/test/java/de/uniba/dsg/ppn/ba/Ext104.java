package de.uniba.dsg.ppn.ba;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.104
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext104 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(createFile("fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "An End Event must not have an outgoing sequence flow",
                "//bpmn:endEvent[0]", 7);
    }

    @Test
    public void testConstraintSuccess() throws ValidatorException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "104";
    }
}
