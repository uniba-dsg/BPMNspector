package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationResult;
import api.ValidationException;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.021
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext021 extends TestCase {

    @Test
    public void testConstraintEventSubProcessFail()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_event_sub_process.bpmn"), 2);
        assertViolation(
                result.getViolations().get(0),
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source",
                "//bpmn:*[./@id = //bpmn:sequenceFlow/@sourceRef and ancestor::bpmn:process][0]", 7);
    }

    @Test
    public void testConstraintEventsSuccess() throws ValidationException {
        verifyValidResult(createFile("success_events.bpmn"));

    }

    @Test
    public void testConstraintGatewaysSuccess() throws ValidationException {
        verifyValidResult(createFile("success_gateways.bpmn"));
    }

    @Test
    public void testConstraintTasksSuccess() throws ValidationException {
        verifyValidResult(createFile("success_tasks.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "021";
    }
}
