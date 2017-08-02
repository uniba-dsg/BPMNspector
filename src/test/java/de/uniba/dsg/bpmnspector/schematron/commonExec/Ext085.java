package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.084
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext085 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT085_failure.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "An optionalInputRef must be listed as dataInputRef.",
                "(//bpmn:inputSet[bpmn:optionalInputRefs])[1]", 7);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT085_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "085";
    }
}
