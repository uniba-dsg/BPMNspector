package de.uniba.dsg.bpmnspector.schematron.analytic;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.091
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext091 extends TestCase {

    private static final String ERR_MSG = "sourceRef and targetRef of a DataAssociation must have the same ItemDefinition or a transformation must be present.";


    @Test
    public void testConstraintFailDifferentStructureRef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT091_failure_differentStructureRef.bpmn"), 1);
        assertViolation(result.getViolations().get(0), ERR_MSG,
                "//bpmn:dataInputAssociation[not(./bpmn:transformation)][0]", 14);
    }

    @Test
    public void testConstraintSuccessEqualItemDef() throws ValidationException {
        verifyValidResult(createFile("EXT091_success_equalItemDef.bpmn"));
    }

    @Test
    public void testConstraintSuccessIdenticalItemDef() throws ValidationException {
        verifyValidResult(createFile("EXT091_success_identicalItemDef.bpmn"));
    }

    @Test
    public void testConstraintSuccessWithTransformation() throws ValidationException {
        verifyValidResult(createFile("EXT091_success_withTransformation.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "091";
    }
}
