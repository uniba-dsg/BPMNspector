package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.114
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext114 extends TestCase {

    private static final String ERR_MSG = "A compensation boundary event MUST NOT have an outgoing Sequence Flow.";


    @Test
    public void testConstraintFailCompensateSeqFlow() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT114_failure_compensateSeqFlow.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "(//bpmn:boundaryEvent[bpmn:compensateEventDefinition])[1]", 16);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT114_success_association.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "114";
    }
}
