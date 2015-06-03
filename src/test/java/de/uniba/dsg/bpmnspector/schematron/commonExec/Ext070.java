package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.070
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext070 extends TestCase {

    private static final String ERR_MSG = "Type of DataInput must be the scalar of the loopDataInput type.";

    @Test
    public void testConstraintFailIsCollection() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT070_failure_isCollection.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:multiInstanceLoopCharacteristics[ancestor::bpmn:process[@isExecutable='true'] and bpmn:loopDataInputRef and bpmn:inputDataItem][0]", 19);
    }

    @Test
    public void testConstraintFailUnequalStructureRef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT070_failure_unequalStructureRef.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:multiInstanceLoopCharacteristics[ancestor::bpmn:process[@isExecutable='true'] and bpmn:loopDataInputRef and bpmn:inputDataItem][0]", 19);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT070_success.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "070";
    }
}
