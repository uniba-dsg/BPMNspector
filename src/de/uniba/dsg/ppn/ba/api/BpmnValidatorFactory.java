package de.uniba.dsg.ppn.ba.api;

import de.uniba.dsg.ppn.ba.validation.BpmnValidator;
import de.uniba.dsg.ppn.ba.validation.SchematronBPMNValidator;

public class BpmnValidatorFactory {

	private static BpmnValidator bpmnValidator;

	static {
		bpmnValidator = new SchematronBPMNValidator();
	}

	public static BpmnValidator getValidatorInstance() {
		return bpmnValidator;
	}

}
