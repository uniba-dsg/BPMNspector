package de.uniba.dsg.ppn.ba.api;

import de.uniba.dsg.ppn.ba.validation.BpmnValidator;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

/**
 * The class is used as api interface to get an instance of the bpmn validator
 * to have the chance for validating files from another project
 * 
 * @author Philipp Neugebauer
 * @version 1.0
 */
public class BpmnValidatorFactory {

	private static BpmnValidator bpmnValidator;

	static {
		bpmnValidator = new SchematronBPMNValidator();
	}

	/**
	 * 
	 * @return the instance of the bpmn validator
	 */
	public static BpmnValidator getValidatorInstance() {
		return bpmnValidator;
	}

}
