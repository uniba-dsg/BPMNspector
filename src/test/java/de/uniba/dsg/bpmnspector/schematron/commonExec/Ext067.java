package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.053
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext067 extends TestCase {

    private static final String ERR_MSG = "If a multiInstance marker is used in an executable process either a " +
            "loopCardinality or a loopDataInputRef must be present.";

    @Test
    public void testConstraintFail() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT067_failure.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "(//bpmn:multiInstanceLoopCharacteristics[ancestor::bpmn:process[@isExecutable='true']])[1]", 10);
    }

    @Test
    public void testConstraintSuccessLoopCardinality() throws ValidationException {
        verifyValidResult(createFile("EXT067_success_loopCardinality.bpmn"));
    }

    @Test
    public void testConstraintSuccessLoopDataInputRef() throws ValidationException {
        verifyValidResult(createFile("EXT067_success_loopDataInputRef.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "067";
    }
}
