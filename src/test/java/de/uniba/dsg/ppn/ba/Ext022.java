package de.uniba.dsg.ppn.ba;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
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
            throws ValidatorException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_event_sub_process.bpmn"), 1);
        assertViolation(
                result.getViolations().get(0),
                "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target",
                "//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][0]", 7);
    }

    @Test
    public void testConstraintEventsSuccess() throws ValidatorException {
        verifyValidResult(createFile("success_events.bpmn"));
    }

    @Test
    public void testConstraintGatewaysSuccess() throws ValidatorException {
        verifyValidResult(createFile("success_gateways.bpmn"));
    }

    @Test
    public void testConstraintTasksSuccess() throws ValidatorException {
        verifyValidResult(createFile("success_tasks.bpmn"));
    }

    @Test
    public void testConstraintTasksSuccessEventProcessInSubProcess()
            throws ValidatorException {
        verifyValidResult(createFile("success_event_in_normal_subprocess.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "022";
    }
}
