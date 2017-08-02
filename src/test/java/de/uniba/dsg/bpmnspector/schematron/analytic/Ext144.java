package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.144
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext144 extends TestCase {

    private static final String ERR_MSG = "The associated Activity must be a Task or a Sub-Process which is marked for " +
            "compensation (i.e., isForCompensation=true)";


    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT144_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "(//bpmn:boundaryEvent[bpmn:compensateEventDefinition])[1]", 5);
    }

    @Override
    protected String getExtNumber() {
        return "144";
    }
}
