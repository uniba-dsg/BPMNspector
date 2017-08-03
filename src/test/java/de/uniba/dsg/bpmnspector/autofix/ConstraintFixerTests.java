package de.uniba.dsg.bpmnspector.autofix;

import api.Location;
import api.LocationCoordinate;
import api.ValidationException;
import api.Violation;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.importer.ProcessImporter;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Collections;

public class ConstraintFixerTests {

    private static final String PATH_PREFIX = Paths.get(System.getProperty("user.dir"))
            .resolve("src/test/resources").toString();

    private ProcessImporter importer = new ProcessImporter();

    private ConstraintFixer fixer;

    @Test
    public void fixingCorrectProcessDoesNotChangeProcess() throws ValidationException {
        BPMNProcess correctProcess = getBpmnProcessForCorrectProcess();
        fixer = new ConstraintFixer(correctProcess, Collections.emptyMap());

        DocHandlingHelper.assertEqualBPMNProcess(correctProcess, fixer.fixAllPossibleIssuesAndReturnProcess());
    }

    @Test
    public void fixingEXT128FailureMarksProcessAsNotExecutable() throws ValidationException {
        BPMNProcess invalidProcess = DocHandlingHelper.loadResource("128/EXT128_failure_endEvent.bpmn");
        Violation singleViolation = new Violation(
                new Location(
                        Paths.get(invalidProcess.getBaseURI()),
                        new LocationCoordinate(11, 7),
                        "(//bpmn:messageEventDefinition[ancestor::bpmn:process[@isExecutable='true']])[1]"),
                "msg",
                "EXT.128");

        fixer = new ConstraintFixer(invalidProcess, Collections.singletonMap(singleViolation, FixingStrategy.AUTO_FIX));
        fixer.fixAllPossibleIssues();

        DocHandlingHelper.assertEqualBPMNProcess(getManuallyFixedProcess("128/EXT128_failure_endEvent_manualFix.bpmn", invalidProcess.getBaseURI()), fixer.getFixedProcess());
    }

    private BPMNProcess getManuallyFixedProcess(String resourceToLoad, String baseUriToUse) throws ValidationException {
        BPMNProcess loaded = DocHandlingHelper.loadResource(resourceToLoad);
        return new BPMNProcess(loaded.getProcessAsDoc(), baseUriToUse, loaded.getNamespace());
    }

    private BPMNProcess getBpmnProcessForCorrectProcess() throws ValidationException {
        return DocHandlingHelper.loadResource("/001/Success.bpmn");
    }



}
