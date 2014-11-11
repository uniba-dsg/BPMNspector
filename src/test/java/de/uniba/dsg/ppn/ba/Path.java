package de.uniba.dsg.ppn.ba;

import java.io.File;

import org.junit.Test;

import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public class Path extends TestCase {

    @Test
    public void testConstraintSuccess1() throws BpmnValidationException {
        File f = new File(getTestFilePath() + File.separator + "path"
                + File.separator + "folder" + File.separator
                + "success_import.bpmn");
        verifyValidResult(f);
    }

    @Test
    public void testConstraintSuccess2() throws BpmnValidationException {
        verifyValidResult(createFile("success_import.bpmn"));
    }

    @Override
    protected String getExtNumber() {
        return "path";
    }
}
