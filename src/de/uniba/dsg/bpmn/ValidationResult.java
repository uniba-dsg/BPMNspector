package de.uniba.dsg.bpmn;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class contains the result of a validation run. If the file is not valid
 * there is a list of violations, which can be used to get the violation
 * messages.
 * 
 * @author Andreas Vorndran, Matthias Geiger
 * @version 1.1
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://www.uniba.de/pi/bpmn-cons/validation", propOrder = {
		"valid", "checkedFiles", "violations" })
@XmlRootElement(name = "validationResult", namespace = "http://www.uniba.de/pi/bpmn-cons/validation")
public class ValidationResult {

	@XmlElement(namespace = "http://www.uniba.de/pi/bpmn-cons/validation")
	private boolean valid;

	@XmlElementWrapper(name = "checkedFile", namespace = "http://www.uniba.de/pi/bpmn-cons/validation")
	@XmlElements(value = { @XmlElement(name = "file", namespace = "http://www.uniba.de/pi/bpmn-cons/validation", type = String.class) })
	private List<String> checkedFiles;

	@XmlElementWrapper(name = "violations", namespace = "http://www.uniba.de/pi/bpmn-cons/validation")
	@XmlElements(value = { @XmlElement(name = "violation", namespace = "http://www.uniba.de/pi/bpmn-cons/validation", type = Violation.class) })
	private List<Violation> violations;

	/**
	 * default constructor for JAXB
	 */
	public ValidationResult() {
		this.checkedFiles = new ArrayList<String>();
		this.violations = new ArrayList<Violation>();
	}

	/**
	 * Constructor
	 * 
	 * @param valid
	 *            are violations found?
	 * @param checkedFiles
	 *            a list of the file names of all checked files
	 * @param violations
	 *            the found violations or null
	 */
	public ValidationResult(boolean valid, List<String> checkedFiles,
			List<Violation> violations) {
		this.valid = valid;
		this.checkedFiles = checkedFiles;
		this.violations = violations;
	}

	/**
	 * @return boolean indicating whether all checked files are valid
	 */
	public boolean isValid() {
		return violations.isEmpty();
	}

	/**
	 * @return list of filenames of all checked files
	 */
	public List<String> getCheckedFiles() {
		return checkedFiles;
	}

	/**
	 * @return a list of all violations
	 */
	public List<Violation> getViolations() {
		return violations;
	}
}
