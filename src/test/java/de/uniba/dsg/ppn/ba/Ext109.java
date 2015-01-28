package de.uniba.dsg.ppn.ba;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.109
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext109 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidatorException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(
                result.getViolations().get(0),
                "If an end event is source of a MessageFlow definition, at least one messageEventDefinition must be present",
                "//bpmn:endEvent[@id = //bpmn:messageFlow/@sourceRef][0]", 16);
    }

    @Test
    public void testConstraintSuccess() throws ValidatorException {
        verifyValidResult(createFile("Success.bpmn"));
    }

    @Test
    public void testConstraintRefSuccess() throws ValidatorException {
        verifyValidResult(createFile("Success_ref.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "109";
    }
}
