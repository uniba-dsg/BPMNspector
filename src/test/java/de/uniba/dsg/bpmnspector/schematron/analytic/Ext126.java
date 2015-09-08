package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.126
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext126 extends TestCase {

    private static final String ERR_MSG = "Links are only allowed to a target in the same process and process level.";


    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT126_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:linkEventDefinition[parent::bpmn:intermediateThrowEvent and bpmn:target][0]", 19);
    }

    @Test
    public void testConstraintSuccessInSubProcess() throws ValidationException {
        verifyValidResult(createFile("EXT126_success_inSubProcess.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "126";
    }
}
