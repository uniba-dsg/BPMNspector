package de.uniba.dsg.ppn.ba;

import java.io.File;
import java.util.List;

import ch.qos.logback.classic.Level;
import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;

public interface BpmnValidator {

	void setLogLevel(Level logLevel);

	ValidationResult validate(File xmlFile) throws BpmnValidationException;

	List<ValidationResult> validateFiles(List<File> xmlFiles)
			throws BpmnValidationException;

}
