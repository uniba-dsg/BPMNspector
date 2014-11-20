package de.uniba.dsg.bpmnspector.refcheck.utils;

import java.util.Properties;

public class ViolationMessageCreator {
	
	public static final int DEFAULT_MSG = 0;
	public static final int PREFIX_MSG = 1;

	/**
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
	 * @param language
	 *            the reference to the language properties (for the violation
	 *            message)
	 */
	public static String createExistenceViolationMessage(String element, String reference, int line,
			int textVersion, String additionalInfo, Properties language) {
		if (textVersion == DEFAULT_MSG) {
			return language.getProperty("existenceviolation.line")
					+ line
					+ " "
					+ language
							.getProperty("existenceviolation.reference.default")
					+ reference + " "
					+ language.getProperty("existenceviolation.element")
					+ element + " "
					+ language.getProperty("existenceviolation.end.default");
		} else if (textVersion == PREFIX_MSG) {
			return language.getProperty("existenceviolation.line")
					+ line
					+ " "
					+ language
							.getProperty("existenceviolation.reference.prefix")
					+ reference + " "
					+ language.getProperty("existenceviolation.element")
					+ element + " "
					+ language.getProperty("existenceviolation.prefix")
					+ additionalInfo + " "
					+ language.getProperty("existenceviolation.end.prefix");
		} else {
			return null;
		}
	}
	
	/**
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
	 * @param language
	 *            the reference to the language properties (for the violation
	 *            message)
	 */
	public static String createTypeViolationMessage(String element, int line, String reference,
			String incorrectType, String expectedType, Properties language) {
		return language.getProperty("typeviolation.reference") + reference
				+ " " + language.getProperty("typeviolation.line") + line + " "
				+ language.getProperty("typeviolation.element") + element + " "
				+ language.getProperty("typeviolation.incorrecttype")
				+ incorrectType
				+ language.getProperty("typeviolation.expectedtype")
				+ expectedType + ".";
	}
}
