package de.uniba.dsg.bpmnspector;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.importer.ProcessImporter;
import de.uniba.dsg.bpmnspector.common.util.FileUtils;
import de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidator;
import de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthias Geiger
 * @version 1.0
 */
public class BPMNspector {

    private final SchematronBPMNValidator extValidator;
    private final BPMNReferenceValidator refValidator;
    private final ProcessImporter bpmnImporter;

    public BPMNspector() throws ValidatorException {
        extValidator = new SchematronBPMNValidator();
        refValidator = new BPMNReferenceValidatorImpl();
        bpmnImporter = new ProcessImporter();
    }

    public List<ValidationResult> inspectDirectory(Path directory, List<ValidationOption> validationOptions) throws ValidatorException {
        List<ValidationResult> results = new ArrayList<>();
        try {
            List<Path> relevantFiles = FileUtils
                    .getAllBpmnFileFromDirectory(directory);
            for (Path path : relevantFiles) {
                    results.add(inspectFile(path, validationOptions));
            }
        } catch (IOException ioe) {
            throw new ValidatorException(
                    "Validation failed due to an IOException.", ioe);
        }

        return results;
    }

    public ValidationResult inspectFile(Path file, List<ValidationOption> validationOptions) throws
            ValidatorException {

        ValidationResult result = new ValidationResult();

        // Trying to generate BPMNProcess structure - XSD validation is always
        // performed is included here
        BPMNProcess process = bpmnImporter
                .importProcessFromPath(file, result);

        if(process!=null) {
            if (validationOptions.contains(ValidationOption.REF)) {
                refValidator.validate(process, result);
            }
            if (validationOptions.contains(ValidationOption.EXT)) {
                extValidator.validate(process, result);
            }
        }

        return result;
    }
}
