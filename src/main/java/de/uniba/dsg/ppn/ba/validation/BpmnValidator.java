package de.uniba.dsg.ppn.ba.validation;

import java.io.File;
import java.util.List;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

/**
 * 
 * @author Philipp Neugebauer
 * 
 */
public interface BpmnValidator {

	/**
	 * Sets the loglevel of all loggers of the bpmn validator to the given level
	 * 
	 * @param logLevel
	 */
	void setLogLevel(Level logLevel);

	/**
	 * checks the given xmlFile for bpmn constraint violations
	 * 
	 * @param xmlFile
	 * @return ValidationResult including all checked files and found violations
	 * @throws BpmnValidationException
	 *             if something fails during validation process
	 */
	ValidationResult validate(File xmlFile) throws BpmnValidationException;

	/**
	 * checks the given xmlFiles for bpmn constraint violations
	 * 
	 * @param xmlFiles
	 * @return List<ValidationResult> including all checked files and found
	 *         violations for each file
	 * @throws BpmnValidationException
	 *             if something fails during validation process
	 */
	List<ValidationResult> validateFiles(List<File> xmlFiles)
			throws BpmnValidationException;

}
