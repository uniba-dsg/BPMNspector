package de.uniba.dsg.bpmnspector.refcheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.jdom2.xpath.XPathHelper;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.bpmnspector.refcheck.importer.FileImporter;
import de.uniba.dsg.bpmnspector.refcheck.importer.ProcessFileSet;
import de.uniba.dsg.bpmnspector.refcheck.utils.ViolationMessageCreator;

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

	public static final int ENGLISH = 0;
	public static final int GERMAN = 1;

	public static final String CONSTRAINT_REF_EXISTENCE = "REF_EXISTENCE";
	public static final String CONSTRAINT_REF_TYPE = "REF_TYPE";

    public static final String EXISTENCE = "existence";
    public static final String REFERENCE = "referenceType";
    /**
	 * Constructor sets the defaults. Log level = OFF and language = ENGLISH.
	 *
	 * @throws ValidatorException
	 *             if problems with the language files exist
	 */
	public BPMNReferenceValidatorImpl() throws ValidatorException {
		setLanguage(ENGLISH);
		setLogLevel(Level.OFF);
		loadReferences();

		bpmnImporter = new FileImporter(language);
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
		return new ValidationResult(valid, checkedFiles,
				violations);
	}

	@Override
	public ValidationResult validateSingleFileExistenceOnly(String path)
			throws ValidatorException {
		ProcessFileSet fileSet = bpmnImporter.loadAllFiles(path, true);
		List<Violation> violations = startValidation(fileSet, EXISTENCE);
		boolean valid = violations.isEmpty();
		List<String> checkedFiles = new ArrayList<>();
		checkedFiles.add(path);
		return new ValidationResult(valid, checkedFiles,
				violations);
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
		if (languageNumber == ENGLISH) {
			language = new Properties();
			try {
				language.load(getClass().getResourceAsStream("/en.lang"));
			} catch (FileNotFoundException e) {
				throw new ValidatorException(
						"Could not find the language file 'lang/en.lang'.", e);
			} catch (IOException e) {
				throw new ValidatorException(
						"IO problems with the language file 'lang/en.lang'.", e);
			}
		} else if (languageNumber == GERMAN) {
			language = new Properties();
			try {
				language.load(getClass().getResourceAsStream("/ger.lang"));
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
		ReferenceLoader referenceLoader = new ReferenceLoader(language, LOGGER);
		bpmnRefElements = referenceLoader.load("/references.xml",
				"/references.xsd");
		StringBuilder bpmnElementsLogText = new StringBuilder();
		for (String key : bpmnRefElements.keySet()) {
            bpmnElementsLogText.append(String.format("%s :: %s", key, bpmnRefElements.get(key)));
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
		Map<String, Map<String, Element>> importedElements = JDOMUtils.getAllElementsGroupedByNamespace(fileSet
				.getReferencedBpmnFiles());

		LOGGER.info(language.getProperty("validator.logger.elements")
				+ System.lineSeparator() + elements.toString());
		StringBuilder importedFilesLogText = new StringBuilder(language.getProperty("validator.logger.importedfiles"))
                .append(System.lineSeparator());
		for (String key : importedElements.keySet()) {
            importedFilesLogText.append(language.getProperty("validator.logger.prefix"))
                    .append(key)
                    .append(System.lineSeparator())
                    .append(importedElements.get(key))
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
								validateExistence(elements, importedElements,
										violationList, currentElement, line,
										checkingReference, referencedId,
										ownPrefix);
							} else if (REFERENCE.equals(validationLevel)) {
								validateReferenceType(elements,
										importedElements, violationList,
										currentElement, line,
										checkingReference, referencedId,
										ownPrefix);
							} else {
                                StringBuilder logText = new StringBuilder(language
                                        .getProperty("validator.illegalargument.validatinglevel.part1"));
                                logText.append(validationLevel).append(' ')
                                        .append(language.getProperty("validator.illegalargument.validatinglevel.part2"));
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
			StringBuilder violationListLogText =
                    new StringBuilder(language.getProperty("validator.logger.violationlist"))
                    .append(System.lineSeparator());
			for (Violation violation : violationList) {
				violationListLogText.append(violation.getMessage()).append(System.lineSeparator());
			}
			LOGGER.info(violationListLogText.toString());
		}
		return violationList;
	}

	/**
	 * This method validates the current element against the checking reference.
	 * Some already existing information will be expected as parameters.
	 *
	 * @param elements
	 *            the elements of the root file (= <code>bpmnFile</code>)
	 * @param violationList
	 *            the violation list for adding found violations
	 * @param currentElement
	 *            the current element to validate
	 * @param line
	 *            the line of the reference in the root file for the violation
	 *            message
	 * @param checkingReference
	 *            the reference to validate against
	 * @param referencedId
	 *            the referenced ID
	 */
	private void validateReferenceType(Map<String, Element> elements,
			Map<String, Map<String, Element>> importedElements,
			List<Violation> violationList, Element currentElement, int line,
			Reference checkingReference, String referencedId, String ownPrefix) {
		if (checkingReference.isQname()) {

            // Check whether the QName is prefixed
			if (referencedId.contains(":")) { 	// reference ID is prefixed and therefore probably to find in an
												// imported file
				String[] parts = referencedId.split(":");
				String prefix = parts[0];
				String importedId = parts[1];

				String namespace = "";

				for (Namespace nsp : currentElement.getNamespacesInScope()) {
					if (nsp.getPrefix().equals(prefix)) {
						namespace = nsp.getURI();
                        break;
					}
				}

				Map<String, Element> relevantImportedElements = importedElements
						.get(namespace);

                // Checking whether the namespace is used for the root and/or the imported file
                // to determine the correct element to be checked
                if (ownPrefix.equals(prefix)) { // namespace is used for the root file

                    // check whether element with ID importedId exists in root or another file
                    if (elements.containsKey(importedId)) { // elem is in root file
                        checkTypeAndAddViolation(violationList, line,
                                currentElement, checkingReference,
                                elements.get(importedId));
                    } else if(relevantImportedElements != null &&
                            relevantImportedElements.containsKey(importedId)) { // element is found in the imported file
                        checkTypeAndAddViolation(violationList, line,
                                currentElement, checkingReference,
                                relevantImportedElements.get(importedId));
                    } else {
                        createAndAddExistenceViolation(violationList, line,
                                currentElement, checkingReference);
                    }

                } else if (relevantImportedElements != null) {  //NOPMD
                    // namespace is used by an imported file
                    if (relevantImportedElements.containsKey(importedId)) {
                        checkTypeAndAddViolation(violationList, line,
                                currentElement, checkingReference,
                                relevantImportedElements
                                        .get(importedId));
                    } else {
                        createAndAddExistenceViolation(violationList, line,
                                currentElement, checkingReference);
                    }
                } else {
                    // invalid namespace: not used in any file
                    // case if the namespace is not used for the root file or an imported file
                    // import does not exist or is no BPMN file (as it has to be)
                    createAndAddExistenceViolation(violationList, line,
                            currentElement, checkingReference);
                }

			} else { // no QName prefix - the elem has to be in the root file
                if (elements.containsKey(referencedId)) {
                    checkTypeAndAddViolation(violationList, line, currentElement,
                            checkingReference, elements.get(referencedId));
                } else {
                    createAndAddExistenceViolation(violationList, line,
                            currentElement, checkingReference);
                }
			}

		} else { // reference uses IDREF - ref has to exist in root file
			if (elements.containsKey(referencedId)) {
                checkTypeAndAddViolation(violationList, line, currentElement,
                        checkingReference, elements.get(referencedId));
			} else {
                createAndAddExistenceViolation(violationList, line,
                        currentElement, checkingReference);
			}
		}
	}

	/**
	 * This method validates that the checking reference of the current element
	 * refers to an existing element. Some already existing information will be
	 * expected as parameters.
	 *
	 * @param elements
	 *            the elements of the root file (= <code>bpmnFile</code>)
	 * @param violationList
	 *            the violation list for adding found violations
	 * @param currentElement
	 *            the current element
	 * @param line
	 *            the line of the reference in the root file for the violation
	 *            message
	 * @param checkingReference
	 *            the reference to validate against
	 * @param referencedId
	 *            the referenced ID
	 */
	private void validateExistence(Map<String, Element> elements,
			Map<String, Map<String, Element>> importedElements,
			List<Violation> violationList, Element currentElement, int line,
			Reference checkingReference, String referencedId, String ownPrefix) {
		if (checkingReference.isQname()) {
			// reference ID is prefixed and therefore probably to find in an
			// imported file
			if (referencedId.contains(":")) {
				String[] parts = referencedId.split(":");
				String prefix = parts[0];
				String importedId = parts[1];
                Map<String, Element> relevantElements = importedElements
						.get(prefix);
				// case if the namespace is used for the root file and an
				// imported file
				if (ownPrefix.equals(prefix) && relevantElements != null) {
					if (!elements.containsKey(importedId)
							&& !relevantElements.containsKey(importedId)) {
						createAndAddExistenceViolation(violationList, line,
								currentElement, checkingReference);
					}
					// case if the namespace is only used for the root file
				} else if (ownPrefix.equals(prefix)) {
					if (!elements.containsKey(importedId)) {
						createAndAddExistenceViolation(violationList, line,
								currentElement, checkingReference);
					}
					// case if the namespace is not used for the roor file or an
					// imported file
				} else if (relevantElements == null) {
					// import does not exist or is no BPMN file (as it has to
					// be)
					createAndAddExistenceViolation(violationList, line,
							currentElement, checkingReference);

					// case if the namespace is used by an imported file
				} else {
					if (relevantElements.get(importedId) == null) {
						createAndAddExistenceViolation(violationList, line,
								currentElement, checkingReference);
					}
				}
				// the referenced element has no prefix and therefore is to find
				// in the root file
			} else {
				if (!elements.containsKey(referencedId)) {
					createAndAddExistenceViolation(violationList, line,
							currentElement, checkingReference);
				}
			}
			// checking reference is IDREF (!QName) and therefore is to find in
			// the root file
		} else {
			if (!elements.containsKey(referencedId)) {
				createAndAddExistenceViolation(violationList, line,
						currentElement, checkingReference);
			}
		}
	}

	/**
	 * This method validates the type of the referenced element against the
	 * checking reference and adds a violation if found.
	 *
	 * @param violationList
	 *            the violation list for adding found violations
	 * @param line
	 *            the line of the reference in the root file for the violation
	 *            message
	 * @param currentElement
	 *            the current element to validate
	 * @param checkingReference
	 *            the reference to validate against
	 * @param referencedElement
	 *            the referenced element to validate
	 */
	private void checkTypeAndAddViolation(List<Violation> violationList,
			int line, Element currentElement, Reference checkingReference,
			Element referencedElement) {
		boolean foundType = false;
		List<String> referencedTypes = checkingReference.getTypes();
		// do not check references which are only for existence validation
		if (referencedTypes != null) {
			// find all possible types (with subtypes/children)
            List<String> types = new ArrayList<>(referencedTypes);
			boolean childfound;
			do {
				childfound = false;
                List<String> typesCopy = new ArrayList<>(types);
				for (String type : typesCopy) {
					if (bpmnRefElements.containsKey(type)) {
						BPMNElement bpmnElement = bpmnRefElements.get(type);
						List<String> children = bpmnElement.getChildren();
						if (children != null) {
							for (String child : children) {
								if (!typesCopy.contains(child)) {
									types.add(child);
									childfound = true;
								}
							}
						}
					}
				}
			} while (childfound);
			// validate if the referenced element has one of the correct types
			for (String type : types) {
				if (referencedElement.getName().equals(type)) {
					foundType = true;
					break;
				}
			}
			if (!foundType) {
				createAndAddReferenceTypeViolation(violationList, line,
						currentElement, checkingReference, referencedElement,
						types);
			} else {
				// special cases for additional checks (look up bachelor thesis
				// for the reference number)
				if (checkingReference.isSpecial()) {
					if (checkingReference.getNumber() == 18
							|| checkingReference.getNumber() == 19) {
                        String isEventSubprocess = referencedElement
								.getAttributeValue("triggeredByEvent");
						if (isEventSubprocess != null
								&& isEventSubprocess.equals("true")) {
							createAndAddReferenceTypeViolation(violationList,
									line, currentElement, checkingReference,
									referencedElement, types);
						}
					} else if (checkingReference.getNumber() == 62) {
						Element parent = currentElement.getParentElement();
						if (parent != null) {
							List<String> tasks = getBPMNTasksList();
							List<String> subprocesses = getBPMNSubProcessList();
							if (tasks.contains(parent.getName())) {
								if (!referencedElement.getName().equals(
										"dataInput")) {
									createAndAddReferenceTypeViolation(
											violationList, line,
											currentElement, checkingReference,
											referencedElement, types);
								}
							} else if (subprocesses.contains(parent.getName())) {
								if (!referencedElement.getName().equals(
										"dataObject")) {
									createAndAddReferenceTypeViolation(
											violationList, line,
											currentElement, checkingReference,
											referencedElement, types);
								}
							}
						}
					} else if (checkingReference.getNumber() == 63) {
						Element parent = currentElement.getParentElement();
						if (parent != null) {
							List<String> tasks = getBPMNTasksList();
							List<String> subprocesses = getBPMNSubProcessList();
							if (tasks.contains(parent.getName())) {
								if (!referencedElement.getName().equals(
										"dataOutput")) {
									createAndAddReferenceTypeViolation(
											violationList, line,
											currentElement, checkingReference,
											referencedElement, types);
								}
							} else if (subprocesses.contains(parent.getName())) {
								if (!referencedElement.getName().equals(
										"dataObject")) {
									createAndAddReferenceTypeViolation(
											violationList, line,
											currentElement, checkingReference,
											referencedElement, types);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @return a list with BPMN task and its subtypes
	 */
	private List<String> getBPMNTasksList() {
		ArrayList<String> tasks = new ArrayList<>();
		tasks.add("task");
		tasks.add("serviceTask");
		tasks.add("sendTask");
		tasks.add("receiveTask");
		tasks.add("userTask");
		tasks.add("manualTask");
		tasks.add("scriptTask");
		tasks.add("businessRuleTask");
		return tasks;
	}

	/**
	 * @return a list with BPMN subProcesses
	 */
	private List<String> getBPMNSubProcessList() {
		ArrayList<String> subprocesses = new ArrayList<>();
		subprocesses.add("subProcess");
		subprocesses.add("adHocSubProcess");
		return subprocesses;
	}

	/**
	 * Adds a found existence violation to the list.
	 *
	 * @param violationList
	 *            the violation list for adding the found violation
	 * @param line
	 *            the line of the reference in the root file
	 * @param currentElement
	 *            the element causing the violation
	 * @param checkingReference
	 *            the violated reference
	 */
	private void createAndAddExistenceViolation(List<Violation> violationList,
			int line, Element currentElement, Reference checkingReference) {
        String message = ViolationMessageCreator
                .createExistenceViolationMessage(currentElement.getName(),
                        checkingReference.getName(), line,
                        ViolationMessageCreator.DEFAULT_MSG,
						XPathHelper.getAbsolutePath(currentElement), language);
		Violation violation = new Violation(CONSTRAINT_REF_EXISTENCE,
				JDOMUtils.getUriFromElement(currentElement), line,
				XPathHelper.getAbsolutePath(currentElement), message);
		violationList.add(violation);
	}

	/**
	 * Creates and adds a found reference type violation to the list of
	 * violations.
	 *
	 * @param violationList
	 *            the violation list for adding the found violation
	 * @param line
	 *            the line of the reference in the root file
	 * @param currentElement
	 *            the element causing the violation
	 * @param checkingReference
	 *            the violated reference
	 * @param referencedElement
	 *            the actually referenced element
	 * @param types
	 *            a list of allowed reference types
	 */
	private void createAndAddReferenceTypeViolation(
			List<Violation> violationList, int line, Element currentElement,
			Reference checkingReference, Element referencedElement,
			List<String> types) {

		String message = ViolationMessageCreator.createTypeViolationMessage(
				currentElement.getName(), line, checkingReference.getName(),
				referencedElement.getName(), types.toString(), language);

		Violation violation = new Violation(CONSTRAINT_REF_TYPE,
				JDOMUtils.getUriFromElement(currentElement), line,
				XPathHelper.getAbsolutePath(currentElement), message);

		violationList.add(violation);
	}

}
