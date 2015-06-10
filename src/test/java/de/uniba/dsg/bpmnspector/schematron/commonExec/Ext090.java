package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.090
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext090 extends TestCase {

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT090_failure.bpmn"),
                1);
        assertViolation(result.getViolations().get(0),
                "A whileExecutingOutputRef must be listed as dataOutputRef.",
                "//bpmn:outputSet[bpmn:whileExecutingOutputRefs][0]", 11);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT090_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "090";
    }
}
