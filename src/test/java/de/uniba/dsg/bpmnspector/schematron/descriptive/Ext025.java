package de.uniba.dsg.bpmnspector.schematron.descriptive;

import api.ValidationResult;
import api.ValidationException;
import api.Violation;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.025
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class Ext025 extends TestCase {

    private static final String XPATHSTRING = "//bpmn:sequenceFlow[bpmn:conditionExpression] [not(@sourceRef = //bpmn:exclusiveGateway/@id)] [not(@sourceRef = //bpmn:parallelGateway/@id)] [not(@sourceRef = //bpmn:inclusiveGateway/@id)] [not(@sourceRef = //bpmn:complexGateway/@id)] [not(@sourceRef = //bpmn:eventBasedGateway/@id)][0]";

    @Test
    public void testConstraintNoIncomingFail() throws ValidationException {
        ValidationResult result = verifyInValidResult(createFile("fail.bpmn"),
                1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintNoIncomingFail2() throws ValidationException {
        ValidationResult result = verifyInValidResult(
                createFile("fail_2.bpmn"), 1);
        assertViolation(result.getViolations().get(0));
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("success.bpmn"));
    }

    @Test
    public void testConstraintSuccess2() throws ValidationException {
        verifyValidResult(createFile("success_2.bpmn"));
    }

    @Test
    public void testConstraintSuccessNoCondition()
            throws ValidationException {
        verifyValidResult(createFile("success_no_condition.bpmn"));
    }

    private void assertViolation(Violation v) {
        assertViolation(v, XPATHSTRING, 19);
    }

    @Override
    protected String getErrorMessage() {
        return "An Activity must not have only one outgoing conditional sequence flow if conditionExpression is present";
    }

    @Override
    protected String getExtNumber() {
        return "025";
    }
}
