package de.uniba.dsg.bpmnspector;

import api.SimpleValidationResult;
import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.importer.ProcessImporter;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MojoTestProcesses {

    private ProcessImporter importer = new ProcessImporter();
    private MojoValidator validator = new MojoValidator();

    @Test
    public void eventbasedSplitMergedByExclusiveGatewayIsCorrect() throws ValidationException {
        String fileToValidate = "valid_eventbased-split_XOR-merge.bpmn";

        ValidationResult result = validateFile(fileToValidate);

        assertTrue(result.isValid());

        // Used Intermediate Timer event is interpreted as a task --> warning is created
        assertEquals(1, result.getWarnings().size());
        assertEquals("mojo: Mojo checks have been performed but results might be wrong as the following elements are unsupported: IntermediateCatchEvent", result.getWarnings().get(0).getMessage());
    }

    @Test
    public void simpleXorSplitWith2EndEventsIsCorrect() throws ValidationException {
        String fileToValidate = "valid_simpleXorSplit2Ends.bpmn";

        ValidationResult result = validateFile(fileToValidate);

        assertTrue(result.isValid());
    }

    @Test
    public void multipleParallelIsCorrect() throws ValidationException {
        String fileToValidate = "valid_multipleParallel.bpmn";

        ValidationResult result = validateFile(fileToValidate);

        assertTrue(result.isValid());
    }

    @Test
    public void parallelSplitIorMergeIsCorrect() throws ValidationException {
        String fileToValidate = "valid_parSplitIORMerge.bpmn";

        ValidationResult result = validateFile(fileToValidate);

        assertTrue(result.isValid());
    }

    @Test
    public void multipleParSplitCrossMergeIsCorrect() throws ValidationException {
        String fileToValidate = "valid_multipleParSplitCrossMerge.bpmn";

        ValidationResult result = validateFile(fileToValidate);

        assertTrue(result.isValid());
    }

    @Test
    public void multipleParallelXorCrossSplitMergeIsCorrect() throws ValidationException {
        String fileToValidate = "valid_multipleParallelXorCrossSplitMerge.bpmn";

        ValidationResult result = validateFile(fileToValidate);

        assertTrue(result.isValid());
    }

    @Test
    public void inclusiveOrSplitMergedByParallelGatewayContainsDeadlock() throws ValidationException {
        String fileToValidate = "invalid_IOR-Split_Par-Merge.bpmn";

        ValidationResult result = validateFile(fileToValidate);

        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        assertEquals("Deadlock", result.getViolations().get(0).getConstraint());
    }

    @Test
    public void multipleParallelXorMixedContainsLackOfSync() throws ValidationException {
        String fileToValidate = "invalid_multipleParallelXorMixed.bpmn";

        ValidationResult result = validateFile(fileToValidate);

        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        assertEquals("LackOfSync", result.getViolations().get(0).getConstraint());
    }

    @Test
    public void loopWithParallelSplitHasLackOfSync() throws ValidationException {
        String fileToValidate = "invalid_LoopWithParallelSplit.bpmn";

        ValidationResult result = validateFile(fileToValidate);

        assertFalse(result.isValid());
        assertEquals(1, result.getViolations().size());
        assertEquals("LackOfSync", result.getViolations().get(0).getConstraint());
    }

    @Test
    public void moreComplexLoopWithParallelSplitHasLackOfSync() throws ValidationException {
        String fileToValidate = "invalid_moreComplexLoopParSplit.bpmn";

        ValidationResult result = validateFile(fileToValidate);

        assertFalse(result.isValid());
        assertEquals(2, result.getViolations().size());
        assertEquals("LackOfSync", result.getViolations().get(0).getConstraint());
        assertEquals("LackOfSync", result.getViolations().get(1).getConstraint());
    }

    @Test
    public void usageOfIntermediateEventsProducesWarning() throws ValidationException {
        String fileToValidate = "warning_intermediateEvents.bpmn";
        String expectedWarning = "mojo: Mojo checks have been performed but results might be wrong as the following " +
                "elements are unsupported: IntermediateCatchEvent, ThrowEvent";

        ValidationResult result = validateFile(fileToValidate);

        assertTrue(result.isValid());
        assertEquals(1, result.getWarnings().size());
        assertEquals(expectedWarning, result.getWarnings().get(0).getMessage());
    }

    @Test
    public void usageOfSubProcessProducesWarning() throws ValidationException {
        String fileToValidate = "warning_subprocess.bpmn";
        String expectedWarning = "mojo: The process contains at least one SubProcess element that, has been treated as a " +
                "simple Task. Therefore, potential problems within the SubProcess are not detected.";

        ValidationResult result = validateFile(fileToValidate);

        assertTrue(result.isValid());
        assertEquals(1, result.getWarnings().size());
        assertEquals(expectedWarning, result.getWarnings().get(0).getMessage());
    }

    @Test
    public void usageOfBoundaryEventsCancelsMojoAndProducesWarning() throws ValidationException {
        String fileToValidate = "warning_boundaryEvent.bpmn";
        String expectedWarning = "mojo: Unable to perform valid mojo execution. Boundary Events attached to tasks are not supported.";

        ValidationResult result = validateFile(fileToValidate);

        assertTrue(result.isValid());
        assertEquals(1, result.getWarnings().size());
        assertEquals(expectedWarning, result.getWarnings().get(0).getMessage());
    }

    @Test
    public void usageOfConditionalSeqFlowCancelsMojoAndProducesWarning() throws ValidationException {
        String fileToValidate = "warning_conditionalSeqFlow.bpmn";
        String expectedWarning = "mojo: Unable to perform valid mojo execution. Conditional sequence flows attached to tasks are not supported.";

        ValidationResult result = validateFile(fileToValidate);

        assertTrue(result.isValid());
        assertEquals(1, result.getWarnings().size());
        assertEquals(expectedWarning, result.getWarnings().get(0).getMessage());
    }

    @Test
    public void multipleProcessFileProducesWarning() throws ValidationException {
        String fileToValidate = "warning_mojo-cross-send-receive.bpmn";
        String expectedWarning = "mojo: BPMN model contains more than one participant or process definition.mojo is not " +
                "capable of analyzing collaborations or BPMN files containing more than one process correctly.Processes " +
                "have been checked singularly instead.";

        ValidationResult result = validateFile(fileToValidate);

        assertTrue(result.isValid());
        assertEquals(1, result.getWarnings().size());
        assertEquals(expectedWarning, result.getWarnings().get(0).getMessage());
    }

    private ValidationResult validateFile(String filepath) throws ValidationException {
        ValidationResult result = new SimpleValidationResult();
        BPMNProcess process = importProcess(filepath, result);
        validator.validate(process, result);
        return result;
    }

    private BPMNProcess importProcess(String filepath, ValidationResult result) throws ValidationException {
        Path path = Paths.get("src/test/resources/mojo/"+filepath);
        return importer.importProcessFromPath(path, result);
    }
}
