package de.uniba.dsg.bpmnspector;

import api.ValidationException;
import api.ValidationResult;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;

public interface BpmnProcessValidator {

    void validate(BPMNProcess process, ValidationResult validationResult) throws ValidationException;
}
