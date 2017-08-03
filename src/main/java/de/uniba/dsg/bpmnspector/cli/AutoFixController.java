package de.uniba.dsg.bpmnspector.cli;

import api.SimpleValidationResult;
import api.ValidationException;
import api.ValidationResult;
import api.Violation;
import de.uniba.dsg.bpmnspector.FixOption;
import de.uniba.dsg.bpmnspector.autofix.ConstraintFixer;
import de.uniba.dsg.bpmnspector.autofix.FixReport;
import de.uniba.dsg.bpmnspector.autofix.FixingStrategy;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.importer.ProcessImporter;
import de.uniba.dsg.bpmnspector.common.util.XmlWriterApi;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AutoFixController {

    public FixReport fixProblems(ValidationResult result, FixOption fixOption, Path path) throws ValidationException {
        Objects.requireNonNull(result);
        Objects.requireNonNull(fixOption);
        Objects.requireNonNull(path);

        if(fixOption == FixOption.NONE || result.isValid()) {
            // skip if fixing is deactivated or there is nothing to fix
            return FixReport.createUnchangedFixReport();
        }

        Map<Violation, FixingStrategy> violationFixingStrategyMap;
        if (fixOption == FixOption.INTERACTIVE) {
            // TODO determine issues to be fixed
            violationFixingStrategyMap = Collections.emptyMap();
        } else {
            violationFixingStrategyMap = createAllAutoFixingMap(result.getViolations());
        }

        ProcessImporter importer = new ProcessImporter();
        BPMNProcess processToFix = importer.importProcessFromPath(path, new SimpleValidationResult(), false);

        ConstraintFixer fixer = new ConstraintFixer(processToFix, violationFixingStrategyMap);
        fixer.fixAllPossibleIssues();
        Path targetPath = path.getParent().resolve("fixed_"+ path.getFileName().toString());
        try {
            XmlWriterApi.writeBPMNProcess(fixer.getFixedProcess(), targetPath);
        } catch (IOException e) {
            throw new ValidationException("Writing fixed process failed."+e);
        }

        return fixer.getGlobalFixReport();
    }

    private static Map<Violation, FixingStrategy> createAllAutoFixingMap(List<Violation> violations) {
        return violations.stream().collect(Collectors.toMap(violation -> violation, violation -> FixingStrategy.AUTO_FIX, (a, b) -> b));
    }

}
