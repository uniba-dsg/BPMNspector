package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.019
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext020 extends TestCase {

    private static final String ERR_MSG = "The attribute isCollection must match the definition of the referenced itemDefinition.";

    @Test
    public void testConstraintFailDataInput() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT020_failure_dataInput.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:dataInput[@itemSubjectRef][0]", 12);
    }

    @Test
    public void testConstraintFailDataInputDefaultValue() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT020_failure_dataInput_defaultValue.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:dataInput[@itemSubjectRef][0]", 12);
    }

    @Test
    public void testConstraintFailDataOutput() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT020_failure_dataOutput.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:dataOutput[@itemSubjectRef][0]", 12);
    }

    @Test
    public void testConstraintFailDataObject() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT020_failure_dataObject.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:dataObject[@itemSubjectRef][0]", 32);
    }

    @Test
    public void testConstraintSuccess() throws ValidationException {
        verifyValidResult(createFile("EXT020_success.bpmn"));
    }

    @Test
    public void testConstraintSuccessNoCollection() throws ValidationException {
        verifyValidResult(createFile("EXT020_success_noCollection.bpmn"));
    }

    @Test
    public void testConstraintSuccessNoCollectionDefaultValues() throws ValidationException {
        verifyValidResult(createFile("EXT020_success_noCollection_defaultValues.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "020";
    }
}
