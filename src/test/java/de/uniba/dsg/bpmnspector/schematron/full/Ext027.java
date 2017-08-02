package de.uniba.dsg.bpmnspector.schematron.full;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.027
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext027 extends TestCase {

    private static final String ERR_MSG = "A choreography or a GlobalConversation must not reference a choreography.";

    @Test
    public void testConstraintFailChoreography() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT027_failure_choreography.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:choreography[bpmn:choreographyRef])[1]", 3);
    }

    @Test
    public void testConstraintFailGlobalConversation() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT027_failure_globalConversation.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:globalConversation[bpmn:choreographyRef])[1]", 14);
    }

    @Test
    public void testConstraintSuccessChorNoRef() throws ValidationException {
        verifyValidResult(createFile("EXT027_success_chorNoRef.bpmn"));
    }

    @Test
    public void testConstraintSuccessGlobalConversation() throws ValidationException {
        verifyValidResult(createFile("EXT027_success_globalConversation.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "027";
    }
}
