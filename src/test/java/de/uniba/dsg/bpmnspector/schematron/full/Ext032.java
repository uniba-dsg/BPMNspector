package de.uniba.dsg.bpmnspector.schematron.full;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.032
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext032 extends TestCase {

    private static final String ERR_MSG = "A globalConversation must not contain any ConversationNodes, " +
            "participantAssociations, messageFlowAssociations, conversationAssociations or artifacts.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT032_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:globalConversation)[1]", 3);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT032_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "032";
    }
}
