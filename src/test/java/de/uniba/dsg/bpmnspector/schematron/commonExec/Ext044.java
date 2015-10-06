package de.uniba.dsg.bpmnspector.schematron.commonExec;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.schematron.TestCase;
import org.junit.Test;

/**
 * Test class for testing Constraint EXT.044
 *
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class Ext044 extends TestCase {

    private static final String ERR_MSG = "The ItemDefinition of the DataInput of the ServiceTask and the inMessage "
            + "itemDefinition of the referenced Operation must be equal.";

    @Test
    public void testConstraintFailDifferentItemDefNoStructureRef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT044_failure_differentItemDef_noStructureRef.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:serviceTask[@operationRef and bpmn:ioSpecification/bpmn:dataInput][0]", 17);
    }

    @Test
    public void testConstraintFailDifferentItemDefUnequalStructureRef() throws ValidationException {
        ValidationResult result = verifyInvalidResult(createFile("EXT044_failure_differentItemDef_unequalStructureRef.bpmn"), 1);
        assertViolation(result.getViolations().get(0),
                ERR_MSG,
                "//bpmn:serviceTask[@operationRef and bpmn:ioSpecification/bpmn:dataInput][0]", 17);
    }

    @Test
    public void testConstraintSuccessDifferentItemDefEqualStructureRef() throws ValidationException {
        verifyValidResult(createFile("EXT044_success_differentItemDef_equalStructureRef.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "044";
    }
}
