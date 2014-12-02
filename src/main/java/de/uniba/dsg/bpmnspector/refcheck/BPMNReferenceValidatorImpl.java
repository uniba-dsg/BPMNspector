package de.uniba.dsg.bpmnspector.refcheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.uniba.dsg.bpmnspector.refcheck.utils.JDOMUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.located.LocatedElement;
import org.jdom2.util.IteratorIterable;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.bpmnspector.refcheck.importer.FileImporter;
import de.uniba.dsg.bpmnspector.refcheck.importer.ProcessFileSet;

/**
 * The implementation of the BPMNReferenceValidator. For more information and an
 * example: {@link BPMNReferenceValidator}.
 *
 * @author Andreas Vorndran
 * @version 1.0
 * @see BPMNReferenceValidator
 * @see ValidationResult
 * @see ValidatorException
 *
 */
public class BPMNReferenceValidatorImpl implements BPMNReferenceValidator {

	private Level level;
	private Properties language;
	private Map<String, BPMNElement> bpmnRefElements;
	private Logger LOGGER;

	private final FileImporter bpmnImporter;
    private final ExistenceChecker existenceChecker;
    private final RefTypeChecker refTypeChecker;

	public static final int ENGLISH = 0;
	public static final int GERMAN = 1;

	public static final String EXISTENCE = "existence";
	public static final String REFERENCE = "referenceType";

	/**
	 * Constructor sets the defaults. Log level = OFF and language = ENGLISH.
	 *
	 * @throws ValidatorException
	 *             if problems with the language files exist
	 */
	public BPMNReferenceValidatorImpl() throws ValidatorException {
        loadLanguage(ENGLISH);
        setLogLevel(Level.OFF);
		loadReferences();

        bpmnImporter = new FileImporter(language);
        existenceChecker = new ExistenceChecker(language);
        refTypeChecker = new RefTypeChecker(language, existenceChecker, bpmnRefElements);
	}

	@Override
	public ValidationResult validate(String path) throws ValidatorException {
		List<Violation> violations = new LinkedList<>();

		ProcessFileSet fileSet = bpmnImporter.loadAllFiles(path, true);

		boolean valid = true;

		for (String filePath : fileSet.getProcessedFiles()) {
			ProcessFileSet fileSetImport = bpmnImporter.loadAllFiles(filePath,
					true);
			List<Violation> importedFileViolations = startValidation(
					fileSetImport, REFERENCE);
			valid = valid && importedFileViolations.isEmpty();
			violations.addAll(importedFileViolations);
		}

		return new ValidationResult(valid, fileSet.getProcessedFiles(),
				violations);
	}

	@Override
	public ValidationResult validateExistenceOnly(String path)
			throws ValidatorException {

		List<Violation> violations = new LinkedList<>();

		ProcessFileSet fileSet = bpmnImporter.loadAllFiles(path, true);

		boolean valid = true;

		for (String filePath : fileSet.getProcessedFiles()) {
			ProcessFileSet fileSetImport = bpmnImporter.loadAllFiles(filePath,
					true);
			List<Violation> importedFileViolations = startValidation(
					fileSetImport, EXISTENCE);
			valid = valid && importedFileViolations.isEmpty();
			violations.addAll(importedFileViolations);
		}

		return new ValidationResult(valid, fileSet.getProcessedFiles(),
				violations);
	}

	@Override
	public ValidationResult validateSingleFile(String path)
			throws ValidatorException {
		ProcessFileSet fileSet = bpmnImporter.loadAllFiles(path, true);
		List<Violation> violations = startValidation(fileSet, REFERENCE);
		boolean valid = violations.isEmpty();
		List<String> checkedFiles = new ArrayList<>();
		checkedFiles.add(path);
		return new ValidationResult(valid, checkedFiles, violations);
	}

	@Override
	public ValidationResult validateSingleFileExistenceOnly(String path)
			throws ValidatorException {
		ProcessFileSet fileSet = bpmnImporter.loadAllFiles(path, true);
		List<Violation> violations = startValidation(fileSet, EXISTENCE);
		boolean valid = violations.isEmpty();
		List<String> checkedFiles = new ArrayList<>();
		checkedFiles.add(path);
		return new ValidationResult(valid, checkedFiles, violations);
	}

	@Override
	public void setLogLevel(Level level) {
		if (LOGGER != null) {
			LOGGER.setLevel(level);
		}
		this.level = level;
	}

	@Override
	public void setLanguage(int languageNumber) throws ValidatorException {
        loadLanguage(languageNumber);
        refTypeChecker.setLanguage(language);
        existenceChecker.setLanguage(language);
        bpmnImporter.setLanguage(language);
	}

    private void loadLanguage(int languageNumber) throws ValidatorException{
        if (languageNumber == ENGLISH) {
            language = new Properties();
            try(InputStream stream = getClass().getResourceAsStream("/en.lang")) {
                language.load(stream);
            } catch (FileNotFoundException e) {
                throw new ValidatorException(
                        "Could not find the language file 'lang/en.lang'.", e);
            } catch (IOException e) {
                throw new ValidatorException(
                        "IO problems with the language file 'lang/en.lang'.", e);
            }
        } else if (languageNumber == GERMAN) {
            language = new Properties();
            try(InputStream stream = getClass().getResourceAsStream("/ger.lang")) {
                language.load(stream);
            } catch (FileNotFoundException e) {
                throw new ValidatorException(
                        "Die Sprachdatei 'lang/ger.lang' kann nicht gefunden werden.",
                        e);
            } catch (IOException e) {
                throw new ValidatorException(
                        "Es sind I/O-Probleme bei der Sprachdatei 'lang/ger.lang' aufgetreten.",
                        e);
            }
        } else {
            throw new ValidatorException(
                    "The desired language is not available. Please choose ENGLISH or GERMAN.");
        }
    }

	/**
	 * This method loads the BPMN elements with the checkable references for the
	 * validation.
	 *
	 * @throws ValidatorException
	 *             if technical problems with the files "references.xml" and
	 *             "references.xsd" occurred
	 */
	private void loadReferences() throws ValidatorException {
		LOGGER = ValidationLoggerFactory.createLogger(null, level, language);
		ReferenceLoader referenceLoader = new ReferenceLoader(language);
		bpmnRefElements = referenceLoader.load("/references.xml",
				"/references.xsd");
		StringBuilder bpmnElementsLogText = new StringBuilder(500);
		for (String key : bpmnRefElements.keySet()) {
			bpmnElementsLogText.append(String.format("%s :: %s", key,
					bpmnRefElements.get(key)));
			bpmnElementsLogText.append(System.lineSeparator());
		}
		LOGGER.info(language.getProperty("validator.logger.bpmnelements")
				+ System.lineSeparator() + bpmnElementsLogText);
	}

	/**
	 * This method validates the BPMN file given through the path for the given
	 * validation level. It is the entrance point for the validation. Therefore
	 * it is used by the public methods of the interface.
	 *
	 * @param fileSet
	 *            the ProcessFileSet of the file, which should be validated
	 * @param validationLevel
	 *            the validation level as String: "existence" or "referenceType"
	 * @return a list of violations, which can be empty if no violations were
	 *         found
	 * @throws ValidatorException
	 *             if technical problems occurred
	 */
	private List<Violation> startValidation(ProcessFileSet fileSet,
			String validationLevel) throws ValidatorException {

		List<Violation> violationList = new ArrayList<>();
		// TODO improve logging
		File file = new File(fileSet.getBpmnBaseFile().getBaseURI());
		LOGGER = ValidationLoggerFactory.createLogger(file.getName(), level,
				language);

		Document baseDocument = fileSet.getBpmnBaseFile();

		// Get all Elements to Check from Base BPMN Process
		Map<String, Element> elements = JDOMUtils.getAllElements(baseDocument);

		String ownPrefix = "";
		// special case if a prefix is used for the target namespace
		Element rootNode = baseDocument.getRootElement();
		String targetNamespace = rootNode.getAttributeValue("targetNamespace");
		for (Namespace namespace : rootNode.getNamespacesInScope()) {
			if (targetNamespace.equals(namespace.getURI())) {
				ownPrefix = namespace.getPrefix();
				break;
			}
		}

		LOGGER.fine("ownprefix after getAllElements():" + ownPrefix);

		// Store all Elements and their IDs in referenced Files into nested
		// Map:
		// outerKey: namespace, innerKey: Id
		Map<String, Map<String, Element>> importedElements = JDOMUtils
				.getAllElementsGroupedByNamespace(fileSet
						.getReferencedBpmnFiles());

		LOGGER.info(language.getProperty("validator.logger.elements")
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

		LOGGER.info(importedFilesLogText.toString());
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
						LOGGER.info(language
								.getProperty("validator.logger.checkingreference")
								+ System.lineSeparator()
								+ currentName
								+ "  ::   " + checkingReference);
						// try to get the reference ID
						String referencedId = null;
						int line = -1;
						if (checkingReference.isAttribute()) {
							referencedId = currentElement
									.getAttributeValue(checkingReference
											.getName());
							line = ((LocatedElement) currentElement).getLine();
						} else {
							for (Element child : currentElement.getChildren()) {
								if (child.getName().equals(
										checkingReference.getName())) {
									referencedId = child.getText();
									line = ((LocatedElement) child).getLine();
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
                                        violationList, currentElement, line,
                                        checkingReference, referencedId,
                                        ownPrefix);
							} else if (REFERENCE.equals(validationLevel)) {
								refTypeChecker.validateReferenceType(elements,
                                        importedElements, violationList,
                                        currentElement, line,
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
								LOGGER.severe(logText.toString());
								throw new ValidatorException(logText.toString());
							}
						}
					}
				}
			}
		}
		// log violations
		if (!violationList.isEmpty()) {
			StringBuilder violationListLogText = new StringBuilder(
					language.getProperty("validator.logger.violationlist"))
					.append(System.lineSeparator());
			for (Violation violation : violationList) {
				violationListLogText.append(violation.getMessage()).append(
						System.lineSeparator());
			}
			LOGGER.info(violationListLogText.toString());
		}
		return violationList;
	}



}
