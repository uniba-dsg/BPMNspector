package de.uniba.dsg.bpmnspector.refcheck;

import api.ValidationResult;
import api.ValidationException;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;

import java.util.logging.Level;

/**
 * This interface specifies the functionality provided by the application layer.
 * The API use of the validator starts with this interface. All needed methods
 * to use the validator are here declared. Log level and language need to set
 * before the validation starts for taking effect. If they were not explicitly
 * set, defaults are used (OFF and ENGLISH). A possible use of the validator
 * might look like the following:<br />
 * <code>
 * ... <br />
 * BPMNReferenceValidator validator = new BPMNReferenceValidatorImpl();<br />
 * validator.setLogLevel(Level.SEVERE);<br />
 * validator.setLanguage(BPMNReferenceValidatorImpl.GERMAN);<br />
 * validator.validate("path/to/file.bpmn");<br />
 * ...
 * </code>
 * 
 * @author Andreas Vorndran
 * @author Matthias Geiger
 * @version 1.0
 * @see BPMNReferenceValidatorImpl
 * @see ValidationResult
 * @see ValidationException
 * 
 */
public interface BPMNReferenceValidator {

	/**
	 * This method validates a BPMN file with the given path and all imported
	 * BPMN files for reference types.
	 * 
	 * @param path
	 *            the path to the file
	 * @return a list of validation results with the potential violations for
	 *         each checked file
	 * @throws ValidationException
	 *             if technical problems occurred
	 */
	ValidationResult validate(String path) throws ValidationException;

	/**
	 * This method validates a set of BPMN processes given in the parameter
	 * process regarding reference coo
	 *
	 * @param process
	 * 			the base process which should be checked
	 * @param validationResult
	 * 			the ValidationResult to be used
	 * @throws ValidationException
	 * 				if technical problems occurred
	 */
	void validate(BPMNProcess process, ValidationResult validationResult) throws ValidationException;

	/**
	 * This method validates a BPMN file with the given path and all imported
	 * BPMN files for reference existences.
	 * 
	 * @param path
	 *            the path to the file
	 * @return a list of validation results with the potential violations for
	 *         each checked file
	 * @throws ValidationException
	 *             if technical problems occurred
	 */
	ValidationResult validateExistenceOnly(String path)
			throws ValidationException;

	/**
	 * This method validates only the BPMN file with the given path for
	 * reference types.
	 * 
	 * @param path
	 *            the path to the file
	 * @return the validation result with the potential violations
	 * @throws ValidationException
	 *             if technical problems occurred
	 */
	ValidationResult validateSingleFile(String path) throws ValidationException;

	/**
	 * This method validates only the BPMN file with the given path for
	 * reference existences.
	 * 
	 * @param path
	 *            the path to the file
	 * @return the validation result with the potential violations
	 * @throws ValidationException
	 *             if technical problems occurred
	 */
	ValidationResult validateSingleFileExistenceOnly(String path)
			throws ValidationException;

	/**
	 * This method sets the log level of the logger to the given one.
	 * 
	 * @param level
	 *            the level to set (used: OFF, SEVERE, INFO)
	 */
	void setLogLevel(Level level);

	/**
	 * This method sets the language. Please use the constants (ENGLISH and
	 * GERMAN) in the BPMNReferenceValidatorImpl.
	 * 
	 * @param language
	 *            the number of the language (ENGLISH = 0 and GERMAN = 1)
	 * @throws ValidationException
	 *             if problems occurred while loading the language file
	 */
	void setLanguage(int language) throws ValidationException;
}
