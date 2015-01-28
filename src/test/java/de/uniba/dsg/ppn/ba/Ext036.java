package de.uniba.dsg.ppn.ba;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.036
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext036 extends TestCase {

    private static final String ERRORMESSAGESOURCE = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source";
    private static final String ERRORMESSAGETARGET = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target";

    @Test
    public void testConstraintCallChoreographyFail()
            throws ValidatorException {
        assertTests("fail_call_choreography.bpmn");
    }

    @Test
    public void testConstraintChoreographyTaskFail()
            throws ValidatorException {
        assertTests("fail_choreography_task.bpmn");
    }

    @Test
    public void testConstraintSubChoreographyFail()
            throws ValidatorException {
        assertTests("fail_sub_choreography.bpmn");
    }

    private void assertTests(String fileName) throws ValidatorException {
        ValidationResult result = verifyInValidResult(createFile(fileName), 3);
        assertViolation(result.getViolations().get(0), ERRORMESSAGESOURCE,
                "//bpmn:*[./@id = //bpmn:sequenceFlow/@sourceRef][1]", 10);
        assertViolation(result.getViolations().get(1), ERRORMESSAGETARGET,
                "//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][1]", 10);
        assertViolation(result.getViolations().get(2),
                "A Process must not contain Choreography Activities",
                "//bpmn:process[0]", 3);
    }

    @Override
    protected String getExtNumber() {
        return "036";
    }

}
