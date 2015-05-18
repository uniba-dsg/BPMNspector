package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationResult;
import api.ValidationException;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.022
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext022 extends TestCase {

    @Test
    public void testConstraintEventSubProcessFail()
            throws ValidationException {
        ValidationResult result = verifyInvalidResult(
                createFile("fail_event_sub_process.bpmn"), 2);
        assertViolation(
                result.getViolations().get(0),
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target",
                "//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][0]", 7);
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

    @Test
    public void testConstraintTasksSuccessEventProcessInSubProcess()
            throws ValidationException {
        verifyValidResult(createFile("success_event_in_normal_subprocess.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "022";
    }
}
