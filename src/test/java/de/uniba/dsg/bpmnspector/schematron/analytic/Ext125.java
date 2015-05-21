package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.125
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext125 extends TestCase {

    private static final String ERR_MSG = "A LinkEventDefinition in a Throw Event must have exactly one target Element.";


    @Test
    public void testConstraintFailNoTarget() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT125_failure_noTarget.bpmn"), 2);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:linkEventDefinition[parent::bpmn:intermediateThrowEvent][0]", 19);
    }

    @Override
    protected String getExtNumber() {
        return "125";
    }
}
