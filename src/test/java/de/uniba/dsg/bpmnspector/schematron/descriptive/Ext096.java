package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationResult;
import api.ValidationException;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.096
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext096 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(createFile("Fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "A Start Event must not have an incoming sequence flow",
                "//bpmn:startEvent[0]", 4);
    }

    @Override
    protected String getExtNumber() {
        return "096";
    }
}
