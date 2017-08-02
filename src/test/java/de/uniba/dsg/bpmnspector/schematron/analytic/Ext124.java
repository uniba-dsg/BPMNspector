package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.124
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext124 extends TestCase {

    private static final String ERR_MSG = "A LinkEventDefinition in a Catch Event must have at least one source Element.";


    @Test
    public void testConstraintFailNoSource() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT124_failure_noSource.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "(//bpmn:linkEventDefinition[parent::bpmn:intermediateCatchEvent])[1]", 9);
    }


    @Override
    protected String getExtNumber() {
        return "124";
    }
}
