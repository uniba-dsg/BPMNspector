package de.uniba.dsg.bpmnspector.refcheck.utils;

public class ViolationMessageCreator {
	
	public static final int DEFAULT_MSG = 0;
	public static final int PREFIX_MSG = 1;

	/**
	 * Creates a description of a found ExistenceViolation based on the given parameters
	 *
	 * @param element
	 *            the name of the BPMN element, where the violation occurred
	 * @param reference
	 *            the name of the reference, where the violation occurred
	 * @param line
	 *            the line of the file, where the violation occurred
	 * @param textVersion
	 *            the version of the violation message text (use constants:
	 *            DEFAULT, PREFIX)
	 * @param additionalInfo
	 *            use for additional information for special violation message
	 *            text versions. For the DEFAULT version it is null, for the
	 *            PREFIX it is the prefix of the namespace.
	 * @return the created String describing the ExistenceViolation
	 */
	public static String createExistenceViolationMessage(String element, String reference, int line,
			int textVersion, String additionalInfo) {
		if (textVersion == DEFAULT_MSG) {
			return String.format("Existence Violation: The reference %s in line %d of the element '%s' " +
					"refers to an element which does not exist.", reference, line, element);
		} else if (textVersion == PREFIX_MSG) {
			return String.format("Existence Violation: The reference %s in line %d of the element '%s' " +
					"refers to an element which does not exist. The namespace prefix '%s' refers either to an unsupported file type" +
					"or no file is bound to this namespace.", reference, line, element, additionalInfo);
		} else {
			return null;
		}
	}
	
	/**
	 * Creates a description of a found Reference Type violation based on the given parameters
	 *
	 * @param element
	 *            the name of the BPMN element, where the violation occurred
	 * @param line
	 *            the line of the file, where the violation occurred
	 * @param reference
	 *            the name of the reference, where the violation occurred
	 * @param incorrectType
	 *            the incorrect type, which caused the violation
	 * @param expectedType
	 *            the types expected instead of the incorrect type
	 * @return the created String describing the Reference Type violation
	 */
	public static String createTypeViolationMessage(String element, int line, String reference,
			String incorrectType, String expectedType) {
		return String.format("Reference Violation: The reference %s in line %d of the element '%s' " +
				"has the incorrect type '%s'. Expected: %s", reference, line, element, incorrectType, expectedType);
	}
}
