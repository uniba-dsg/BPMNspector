package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.118
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext118 extends TestCase {

    private static final String ERR_MSG = "For each source Link there must exist a corresponding target. There may be multiple sources for one target.";


    @Test
    public void testConstraintFailNoTarget() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT118_failure_wrongTarget.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "(//bpmn:linkEventDefinition[bpmn:target])[2]", 19);
    }

    @Test
    public void testConstraintSuccessMultipleSources() throws ValidationException {
        verifyValidResult(createFile("EXT118_success_multipleSources.bpmn"));
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT118_success.bpmn"));
    }


    @Override
    protected String getExtNumber() {
        return "118";
    }
}
