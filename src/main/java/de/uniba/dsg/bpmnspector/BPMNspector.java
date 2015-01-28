package de.uniba.dsg.bpmnspector;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import de.uniba.dsg.bpmnspector.common.util.FileUtils;
import de.uniba.dsg.bpmnspector.common.xsdvalidation.BpmnXsdValidator;
import de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidator;
import de.uniba.dsg.bpmnspector.refcheck.BPMNReferenceValidatorImpl;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class BPMNspector {

    private final SchematronBPMNValidator extValidator;
    private final BPMNReferenceValidator refValidator;
    private final BpmnXsdValidator xsdValidator;

    public BPMNspector() throws ValidatorException {
        extValidator = new SchematronBPMNValidator();
        xsdValidator = new BpmnXsdValidator();
        refValidator = new BPMNReferenceValidatorImpl();
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

        if (validationOptions.contains(ValidationOption.XSD)
                && !validationOptions.contains(ValidationOption.REF)
                && !validationOptions.contains(ValidationOption.EXT)) {
            //Perform XSD validation
            try {
                xsdValidator.validateAgainstXsd(file.toFile(), result);
                result.getCheckedFiles().add(file.toAbsolutePath().toString());
            } catch (ValidatorException e) {
                // ValidatorException thrown because File is not well-formed
                // can be ignored here as no further processing is required
            } catch (SAXException | IOException ex) {
                throw new ValidatorException("Schema validation failed due to internal error.", ex);
            }

        }
        if (validationOptions.contains(ValidationOption.REF)) {
            ValidationResult refResult = refValidator.validate(
                    file.toAbsolutePath().toString());
            mergeResults(result, refResult);
        }
        if (validationOptions.contains(ValidationOption.EXT)) {
            try {
                ValidationResult extResult = extValidator.validate(file.toAbsolutePath().toFile());
                mergeResults(result, extResult);
            } catch (ValidatorException ex) {
                //throw new ValidatorException("EXT Validation failed.", ex);
            }
        }

        return result;
    }



    private void mergeResults(ValidationResult target, ValidationResult source) {
        target.getCheckedFiles().addAll(source.getCheckedFiles());
        target.getViolations().addAll(source.getViolations());
        target.setValid(target.isValid() && source.isValid());
    }

}
