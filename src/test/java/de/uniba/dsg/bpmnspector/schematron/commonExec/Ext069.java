package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.069
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext069 extends TestCase {

    private static final String ERR_MSG = "Type of DataOutput must be the scalar of the loopDataOutput type.";

    @Test
    public void testConstraintFailIsCollection() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT069_failure_isCollection.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:multiInstanceLoopCharacteristics[ancestor::bpmn:process[@isExecutable='true'] and bpmn:loopDataOutputRef and bpmn:outputDataItem][0]", 19);
    }

    @Test
    public void testConstraintFailUnequalStructureRef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT069_failure_unequalStructureRef.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:multiInstanceLoopCharacteristics[ancestor::bpmn:process[@isExecutable='true'] and bpmn:loopDataOutputRef and bpmn:outputDataItem][0]", 19);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT069_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "069";
    }
}
