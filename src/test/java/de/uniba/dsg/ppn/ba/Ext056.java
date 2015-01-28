package de.uniba.dsg.ppn.ba;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.056
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext056 extends TestCase {

    private final static String ERRORMESSAGETARGET = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the target. However, Activities that are Event SubProcesses are not allowed to be a target";
    private final static String ERRORMESSAGECOREGRAPHY = "A SubProcess must not contain Choreography Activities";
    private final static String ERRORMESSAGESOURCE = "For a Process: Of the types of FlowNode, only Activities, Gateways, and Events can be the source. However, Activities that are Event SubProcesses are not allowed to be a source";
    private final static String XPATHSTRINGTARGET = "//bpmn:*[./@id = //bpmn:sequenceFlow/@targetRef][1]";
    private final static String XPATHSTRINGSOURCE = "//bpmn:*[./@id = //bpmn:sequenceFlow/@sourceRef][1]";

    @Test
    public void testConstraintCallChoreographyFail()
            throws ValidatorException {
        assertTests("fail_call_choreography.bpmn", "//bpmn:subProcess[0]");
    }

    @Test
    public void testConstraintChoreographyTaskFail()
            throws ValidatorException {
        assertTests("fail_choreography_task.bpmn", "//bpmn:subProcess[0]");
    }

    @Test
    public void testConstraintChoreographyTaskTransactionFail()
            throws ValidatorException {
        assertTests("fail_choreography_task_transaction.bpmn",
                "//bpmn:transaction[0]");
    }

    @Test
    public void testConstraintSubChoreographyFail()
            throws ValidatorException {
        assertTests("fail_sub_choreography.bpmn", "//bpmn:subProcess[0]");
    }

    private void assertTests(String fileName, String xpath)
            throws ValidatorException {
        ValidationResult result = verifyInValidResult(createFile(fileName), 3);
        assertViolation(result.getViolations().get(0), ERRORMESSAGESOURCE,
                XPATHSTRINGSOURCE, 11);
        assertViolation(result.getViolations().get(1), ERRORMESSAGETARGET,
                XPATHSTRINGTARGET, 11);
        assertViolation(result.getViolations().get(2), ERRORMESSAGECOREGRAPHY,
                xpath, 4);
    }

    @Override
    protected String getExtNumber() {
        return "056";
    }
}
