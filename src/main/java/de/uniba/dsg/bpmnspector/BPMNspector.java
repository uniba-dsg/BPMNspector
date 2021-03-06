package de.uniba.dsg.bpmnspector;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import api.UnsortedValidationResult;
import api.ValidationException;
import api.ValidationResult;
import api.Validator;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.importer.ProcessImporter;
import de.uniba.dsg.bpmnspector.common.util.FileUtils;
import de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidator;
import de.uniba.dsg.bpmnspector.schematron.SchematronBPMNValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matthias Geiger
 * @version 1.0
 */
public class BPMNspector implements Validator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BPMNspector.class.getSimpleName());

    private final SchematronBPMNValidator extValidator;
    private final BPMNReferenceValidator refValidator;
    private final MojoValidator mojoValidator;

    private final ProcessImporter bpmnImporter;

    public BPMNspector() throws ValidationException {
        extValidator = new SchematronBPMNValidator();
        refValidator = new BPMNReferenceValidator();
        mojoValidator = new MojoValidator();

        bpmnImporter = new ProcessImporter();
    }

    public List<ValidationResult> inspectDirectory(Path directory, List<ValidationOption> validationOptions) throws ValidationException {
        List<ValidationResult> results = new ArrayList<>();

        List<Path> relevantFiles = FileUtils
                    .getAllBpmnFileFromDirectory(directory);
        for (Path path : relevantFiles) {
                    results.add(inspectFile(path, validationOptions));
        }
        return results;
    }

    public ValidationResult inspectFile(Path file, List<ValidationOption> validationOptions) throws
            ValidationException {

        ValidationResult result = new UnsortedValidationResult();

        // Trying to generate BPMNProcess structure - XSD validation is always
        // performed is included here
        BPMNProcess process = bpmnImporter
                .importProcessFromPath(file, result);

        if(process==null) {
            LOGGER.info("Process could not parsed correctly. Further processing is skipped.");
        } else {
            if (validationOptions.contains(ValidationOption.REF)) {
                refValidator.validate(process, result);
            }
            if (validationOptions.contains(ValidationOption.EXT)) {
                extValidator.validate(process, result);
            }
            if(validationOptions.contains(ValidationOption.MOJO)) {
                mojoValidator.validate(process, result);
            }

        }

        String resultString = result.isValid() ? "valid" : "invalid";
        resultString += result.getWarnings().isEmpty() ? "" : " with warnings";
        LOGGER.info("Overall result for '{}': {}", file.getFileName().toString(), resultString);

        return result;
    }

    public ValidationResult validate(Path path) throws ValidationException {
        List<ValidationOption> options = new ArrayList<>();
        options.add(ValidationOption.EXT);
        options.add(ValidationOption.REF);
        return inspectFile(path, options);
    }

    public ValidationResult validate(InputStream source, String resourceName) throws ValidationException {

        ValidationResult result = new UnsortedValidationResult();

        // Trying to generate BPMNProcess structure - XSD validation is always
        // performed is included here
        BPMNProcess process = bpmnImporter.importProcessFromStreamSource(source, resourceName, result);


        if(process==null) {
            LOGGER.info("Process could not parsed correctly. Further processing is skipped.");
        } else {
            refValidator.validate(process, result);
            extValidator.validate(process, result);
        }
        String resultString = result.isValid() ? "valid" : "invalid";
        LOGGER.info("Overall result for '{}': {}", resourceName, resultString);
        return result;
    }
}
