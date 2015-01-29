package de.uniba.dsg.bpmnspector.refcheck;

import api.*;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.importer.ProcessImporter;
import de.uniba.dsg.bpmnspector.refcheck.utils.JDOMUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.located.LocatedElement;
import org.jdom2.util.IteratorIterable;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

/**
 * The implementation of the BPMNReferenceValidator. For more information and an
 * example: {@link BPMNReferenceValidator}.
 *
 * @author Andreas Vorndran
 * @author Matthias Geiger
 * @version 1.0
 * @see BPMNReferenceValidator
 * @see ValidationResult
 * @see ValidationException
 *
 */
public class BPMNReferenceValidatorImpl implements BPMNReferenceValidator {

	private Properties language;
	private Map<String, BPMNElement> bpmnRefElements;


	private final ProcessImporter bpmnImporter;
    private final ExistenceChecker existenceChecker;
    private final RefTypeChecker refTypeChecker;

	public static final int ENGLISH = 0;
	public static final int GERMAN = 1;

	public static final String EXISTENCE = "existence";
	public static final String REFERENCE = "referenceType";

	private static final String RESULT_TEXT_TEMPLATE = "Reference check of file %s finished; %d violations found.";

	private static final org.slf4j.Logger LOGGER = LoggerFactory
			.getLogger(BPMNReferenceValidator.class.getSimpleName());

	/**
	 * Constructor sets the defaults. Log level = OFF and language = ENGLISH.
	 *
	 * @throws ValidationException
	 *             if problems with the language files exist
	 */
	public BPMNReferenceValidatorImpl() throws ValidationException {
        loadLanguage(ENGLISH);
        setLogLevel(Level.OFF);
		loadReferences();

        bpmnImporter = new ProcessImporter();
        existenceChecker = new ExistenceChecker(language);
        refTypeChecker = new RefTypeChecker(language, existenceChecker, bpmnRefElements);
	}

	@Override
	public ValidationResult validate(String path) throws ValidationException {
		ValidationResult result = new UnsortedValidationResult();
		try {
			BPMNProcess process = bpmnImporter
					.importProcessFromPath(Paths.get(path), result);

			if (process != null) {
				List<BPMNProcess> processesToCheck = new ArrayList<>();

				process.getAllProcessesRecursively(processesToCheck);

				for (BPMNProcess processToCheck : processesToCheck) {
					startValidation(processToCheck, REFERENCE, result);
				}

			}

			String resultText = String
					.format(RESULT_TEXT_TEMPLATE,
							path, result.getViolations().size());
			LOGGER.info(resultText);
		} catch (ValidationException e) {
			LOGGER.error("Validation of process {} failed, due to an error: {}",
					path, e);
		}
		return result;
	}

	@Override
	public void validate(BPMNProcess process, ValidationResult validationResult) throws ValidationException {
		try {
			if (process != null) {
				List<BPMNProcess> processesToCheck = new ArrayList<>();

				process.getAllProcessesRecursively(processesToCheck);

				for (BPMNProcess processToCheck : processesToCheck) {
					startValidation(processToCheck, REFERENCE, validationResult);
				}
			}

			String resultText = String
					.format(RESULT_TEXT_TEMPLATE,
							process.getBaseURI(), validationResult.getViolations().size());
			LOGGER.info(resultText);
		} catch (ValidationException e) {
			LOGGER.error("Validation of process {} failed, due to an error: {}",
					process.getBaseURI(), e);
		}
	}

	@Override
	public ValidationResult validateExistenceOnly(String path)
			throws ValidationException {
		ValidationResult result = new UnsortedValidationResult();
		BPMNProcess process = bpmnImporter.importProcessFromPath(Paths.get(path), result);
		if (process != null) {
			List<BPMNProcess> processesToCheck = new ArrayList<>();

			process.getAllProcessesRecursively(processesToCheck);

			for(BPMNProcess processToCheck : processesToCheck) {
				startValidation(processToCheck, EXISTENCE, result);
			}

		}

		String resultText = String.format(RESULT_TEXT_TEMPLATE, path, result.getViolations().size());
		LOGGER.info(resultText);
		return result;
	}

	@Override
	public ValidationResult validateSingleFile(String path)
			throws ValidationException {
		ValidationResult result = new UnsortedValidationResult();
		BPMNProcess process = bpmnImporter.importProcessFromPath(Paths.get(path), result);
		if (process != null) {
			startValidation(process, REFERENCE, result);
		}
		String resultText = String.format(RESULT_TEXT_TEMPLATE, path, result.getViolations().size());
		LOGGER.info(resultText);
		return result;
	}

	@Override
	public ValidationResult validateSingleFileExistenceOnly(String path)
			throws ValidationException {
		ValidationResult result = new UnsortedValidationResult();
		BPMNProcess process = bpmnImporter.importProcessFromPath(
				Paths.get(path), result);
		if (process != null) {
			startValidation(process, EXISTENCE, result);
		}
		String resultText = String.format(RESULT_TEXT_TEMPLATE, path, result.getViolations().size());
		LOGGER.info(resultText);
		return result;
	}

	@Override
	public void setLogLevel(Level level) {
		// TODO decide what to do with setLogLevel methods
	}

	@Override
	public void setLanguage(int languageNumber) throws ValidationException {
        loadLanguage(languageNumber);
        refTypeChecker.setLanguage(language);
        existenceChecker.setLanguage(language);
	}

    private void loadLanguage(int languageNumber) throws ValidationException{
        if (languageNumber == ENGLISH) {
            language = new Properties();
            try(InputStream stream = getClass().getResourceAsStream("/en.lang")) {
                language.load(stream);
            } catch (FileNotFoundException e) {
                throw new ValidationException(
                        "Could not find the language file 'lang/en.lang'.", e);
            } catch (IOException e) {
                throw new ValidationException(
                        "IO problems with the language file 'lang/en.lang'.", e);
            }
        } else if (languageNumber == GERMAN) {
            language = new Properties();
            try(InputStream stream = getClass().getResourceAsStream("/ger.lang")) {
                language.load(stream);
            } catch (FileNotFoundException e) {
                throw new ValidationException(
                        "Die Sprachdatei 'lang/ger.lang' kann nicht gefunden werden.",
                        e);
            } catch (IOException e) {
                throw new ValidationException(
                        "Es sind I/O-Probleme bei der Sprachdatei 'lang/ger.lang' aufgetreten.",
                        e);
            }
        } else {
            throw new ValidationException(
                    "The desired language is not available. Please choose ENGLISH or GERMAN.");
        }
    }

	/**
	 * This method loads the BPMN elements with the checkable references for the
	 * validation.
	 *
	 * @throws ValidationException
	 *             if technical problems with the files "references.xml" and
	 *             "references.xsd" occurred
	 */
	private void loadReferences() throws ValidationException {
		ReferenceLoader referenceLoader = new ReferenceLoader(language);
		bpmnRefElements = referenceLoader.load("/references.xml",
				"/references.xsd");
		StringBuilder bpmnElementsLogText = new StringBuilder(500);
		for (String key : bpmnRefElements.keySet()) {
			bpmnElementsLogText.append(String.format("%s :: %s", key,
					bpmnRefElements.get(key)));
			bpmnElementsLogText.append(System.lineSeparator());
		}
		LOGGER.debug(language.getProperty("validator.logger.bpmnelements")
				+ System.lineSeparator() + bpmnElementsLogText);
	}

	/**
	 * This method validates the BPMN file given through the path for the given
	 * validation level. It is the entrance point for the validation. Therefore
	 * it is used by the public methods of the interface.
	 *
	 * @param baseProcess
	 *            the ProcessFileSet of the file, which should be validated
	 * @param validationLevel
	 *            the validation level as String: "existence" or "referenceType"
	 * @throws ValidationException
	 *             if technical problems occurred
	 */
	private void startValidation(BPMNProcess baseProcess,
			String validationLevel, ValidationResult validationResult) throws ValidationException {

		Document baseDocument = baseProcess.getProcessAsDoc();

		// Get all Elements to Check from Base BPMN Process
		Map<String, Element> elements = JDOMUtils.getAllElements(baseDocument);

		String ownPrefix = "";
		// special case if a prefix is used for the target namespace
		Element rootNode = baseDocument.getRootElement();
		String targetNamespace = rootNode.getAttributeValue("targetNamespace");
		if(targetNamespace==null) {
			targetNamespace = "";
		}
		for (Namespace namespace : rootNode.getNamespacesInScope()) {
			if (targetNamespace.equals(namespace.getURI())) {
				ownPrefix = namespace.getPrefix();
				break;
			}
		}

		LOGGER.debug("ownprefix after getAllElements():" + ownPrefix);

		// Store all Elements and their IDs in referenced Files into nested
		// Map:
		// outerKey: namespace, innerKey: Id
		Map<String, Map<String, Element>> importedElements = new HashMap<>();
		JDOMUtils.getAllElementsGroupedByNamespace(importedElements,
				baseProcess,
				new ArrayList<>());

		LOGGER.debug(language.getProperty("validator.logger.elements")
				+ System.lineSeparator() + elements.toString());
		StringBuilder importedFilesLogText = new StringBuilder(100)
				.append(language.getProperty("validator.logger.importedfiles"))
				.append(System.lineSeparator());
		for (Map.Entry entry : importedElements.entrySet()) {
			importedFilesLogText
					.append(language.getProperty("validator.logger.prefix"))
					.append(entry.getKey()).append(System.lineSeparator())
					.append(entry.getValue())
					.append(System.lineSeparator());
		}

		LOGGER.debug(importedFilesLogText.toString());
		// get all elements of the file for validate their references
		Filter<Element> filter = Filters.element();
		IteratorIterable<Element> list = baseDocument.getDescendants(filter);
		while (list.hasNext()) {
			Element currentElement = list.next();
			String currentName = currentElement.getName();
			// proof if the current element can have references
			if (bpmnRefElements.containsKey(currentName)) {
				BPMNElement bpmnElement = bpmnRefElements.get(currentName);
				// create list of all inherited elements and their
				// references
				List<BPMNElement> checkingBPMNElements = new ArrayList<>();
				checkingBPMNElements.add(bpmnElement);
				while (bpmnElement.getParent() != null) {
					bpmnElement = bpmnRefElements.get(bpmnElement.getParent());
					checkingBPMNElements.add(bpmnElement);
				}
				// proof each possible reference
				for (BPMNElement checkingElement : checkingBPMNElements) {
					for (Reference checkingReference : checkingElement
							.getReferences()) {
						LOGGER.debug(language
								.getProperty(
										"validator.logger.checkingreference")
								+ System.lineSeparator()
								+ currentName
								+ "  ::   " + checkingReference);
						// try to get the reference ID
						String referencedId = null;
						int line = -1;
						int column = -1;
						if (checkingReference.isAttribute()) {
							referencedId = currentElement
									.getAttributeValue(checkingReference
											.getName());
							line = ((LocatedElement) currentElement).getLine();
							column = ((LocatedElement) currentElement).getColumn();
						} else {
							for (Element child : currentElement.getChildren()) {
								if (child.getName().equals(
										checkingReference.getName())) {
									referencedId = child.getText();
									line = ((LocatedElement) child).getLine();
									column = ((LocatedElement) child).getColumn();
									break;
								}
							}
						}
						// if the current element has the reference start
						// the validation
						if (referencedId != null) {
							if (EXISTENCE.equals(validationLevel)) {
                                existenceChecker.validateExistence(elements,
                                        importedElements,
                                        validationResult, currentElement, line,
                                        column, checkingReference, referencedId,
                                        ownPrefix);
							} else if (REFERENCE.equals(validationLevel)) {
								refTypeChecker.validateReferenceType(elements,
                                        importedElements, validationResult,
                                        currentElement, line, column,
                                        checkingReference, referencedId,
                                        ownPrefix);
							} else {
								StringBuilder logText = new StringBuilder(
										language.getProperty("validator.illegalargument.validatinglevel.part1"));
								logText.append(validationLevel)
										.append(' ')
										.append(language
												.getProperty(
                                                        "validator.illegalargument.validatinglevel.part2"));
								LOGGER.error(logText.toString());
								throw new ValidationException(logText.toString());
							}
						}
					}
				}
			}
		}
		// log violations
		if (!validationResult.isValid()) {
			StringBuilder violationListLogText = new StringBuilder(
					language.getProperty("validator.logger.violationlist"))
					.append(System.lineSeparator());
			for (Violation violation : validationResult.getViolations()) {
				violationListLogText.append(violation.getMessage()).append(
						System.lineSeparator());
			}
			LOGGER.info(violationListLogText.toString());
		}
	}
}
