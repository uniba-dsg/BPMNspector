package de.uniba.dsg.bpmnspector.xsdvalidation.tests;

import de.uniba.dsg.bpmnspector.BPMNspector;
import de.uniba.dsg.bpmnspector.ValidationOption;
import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.refcheck.ValidatorException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class NonWellformedXmlTests {

    private static BPMNspector nspector;

    private static final List<ValidationOption> XSD_ONLY = Arrays
            .asList(ValidationOption.XSD);
    private static final List<ValidationOption> REF_ONLY = Arrays
            .asList(ValidationOption.REF);
    private static final List<ValidationOption> EXT_ONLY = Arrays
            .asList(ValidationOption.EXT);

    @BeforeClass
    public static void setUpBeforeClass() {
        try {
            nspector = new BPMNspector();
        } catch (ValidatorException e) {
            fail();
        }
    }

    @Test
    public void nonWellformedBpmnFileTest() throws ValidatorException {
        Path file = Paths
                .get("src/test/resources/non-wellformed/single_process.bpmn");
        String affectedFile = "single_process.bpmn";
        int affectedLine = 26;

        ValidationResult result = nspector.inspectFile(file, XSD_ONLY);
        assertAffectedFileAndLine(result, affectedFile, affectedLine);

        result = nspector.inspectFile(file, REF_ONLY);
        assertAffectedFileAndLine(result, affectedFile, affectedLine);

        result = nspector.inspectFile(file, EXT_ONLY);
        assertAffectedFileAndLine(result, affectedFile, affectedLine);

    }

    @Test
    public void nonWellformedBpmnFileImportedTest() throws ValidatorException {
        Path file = Paths
                .get("src/test/resources/non-wellformed/import_non_wellformed.bpmn");
        String affectedFile = "single_process.bpmn";
        int affectedLine = 26;

        ValidationResult result = nspector.inspectFile(file, REF_ONLY);
        assertAffectedFileAndLine(result, affectedFile, affectedLine);

        result = nspector.inspectFile(file, EXT_ONLY);
        assertAffectedFileAndLine(result, affectedFile, affectedLine);
    }

    @Test
    public void nonWellformedWsdlFileImportedTest() throws ValidatorException {
        Path file = Paths
                .get("src/test/resources/non-wellformed/import_wsdl_non_wellformed.bpmn");
        String affectedFile = "wsdl2primer_corrupt.wsdl";
        int affectedLine = 16;

        ValidationResult result = nspector.inspectFile(file, EXT_ONLY);
        assertAffectedFileAndLine(result, affectedFile, affectedLine);
    }

    @Test
    public void nonWellformedXsdFileImportedTest() throws ValidatorException {
        Path file = Paths
                .get("src/test/resources/non-wellformed/import_xsd_non_wellformed.bpmn");
        String affectedFile = "simple_corrupt.xsd";
        int affectedLine = 11;

        ValidationResult result = nspector.inspectFile(file, EXT_ONLY);
        assertAffectedFileAndLine(result, affectedFile, affectedLine);
    }



    private void assertAffectedFileAndLine(ValidationResult result, String affectedFile,
            int affectedLine) {
        assertFalse(result.isValid());
        assertEquals(affectedLine, result.getViolations().get(0).getLine());
        assertEquals(affectedFile, result.getViolations().get(0).getFileName());
    }

}
