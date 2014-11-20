package de.uniba.dsg.bpmnspector.refcheck;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Attribute;
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
	private HashMap<String, BPMNElement> bpmnRefElements;
	private Logger LOGGER;

	private final FileImporter bpmnImporter;

	public static final int ENGLISH = 0;
	public static final int GERMAN = 1;

	public static final String CONSTRAINT_REF_EXISTENCE = "REF_EXISTENCE";
	public static final String CONSTRAINT_REF_TYPE = "REF_TYPE";

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
					fileSetImport, "referenceType");
			valid = valid && importedFileViolations.size() == 0;
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
					fileSetImport, "existence");
			valid = valid && importedFileViolations.size() == 0;
			violations.addAll(importedFileViolations);
		}

		return new ValidationResult(valid, fileSet.getProcessedFiles(),
				violations);
	}

	@Override
	public ValidationResult validateSingleFile(String path)
			throws ValidatorException {
		ProcessFileSet fileSet = bpmnImporter.loadAllFiles(path, true);
		List<Violation> violations = startValidation(fileSet, "referenceType");
		boolean valid = violations.size() == 0;
		List<String> checkedFiles = new ArrayList<>();
		checkedFiles.add(path);
		return new ValidationResult(valid, fileSet.getProcessedFiles(),
				violations);
	}

	@Override
	public ValidationResult validateSingleFileExistenceOnly(String path)
			throws ValidatorException {
		ProcessFileSet fileSet = bpmnImporter.loadAllFiles(path, true);
		List<Violation> violations = startValidation(fileSet, "existence");
		boolean valid = violations.size() == 0;
		List<String> checkedFiles = new ArrayList<>();
		checkedFiles.add(path);
		return new ValidationResult(valid, fileSet.getProcessedFiles(),
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
		String bpmnElementsLogText = "";
		for (String key : bpmnRefElements.keySet()) {
			bpmnElementsLogText = bpmnElementsLogText + key + " :: "
					+ bpmnRefElements.get(key) + System.lineSeparator();
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
		HashMap<String, Element> elements = getAllElements(baseDocument);

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
		// HashMap:
		// outerKey: namespace, innerKey: Id
		HashMap<String, HashMap<String, Element>> importedElements = getAllElementsGroupedByNamespace(fileSet
				.getReferencedBpmnFiles());

		LOGGER.info(language.getProperty("validator.logger.elements")
				+ System.lineSeparator() + elements.toString());
		String importedFilesLogText = "";
		for (String key : importedElements.keySet()) {
			importedFilesLogText = importedFilesLogText
					+ language.getProperty("validator.logger.prefix") + key
					+ System.lineSeparator() + importedElements.get(key)
					+ System.lineSeparator();
		}
		LOGGER.info(language.getProperty("validator.logger.importedfiles")
				+ System.lineSeparator() + importedFilesLogText);
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
							if ("existence".equals(validationLevel)) {
								validateExistence(elements, importedElements,
										violationList, currentElement, line,
										checkingReference, referencedId,
										ownPrefix);
							} else if ("referenceType".equals(validationLevel)) {
								validateReferenceType(elements,
										importedElements, violationList,
										currentElement, line,
										checkingReference, referencedId,
										ownPrefix);
							} else {
								LOGGER.severe(language
										.getProperty("validator.illegalargument.validatinglevel.part1")
										+ validationLevel
										+ " "
										+ language
												.getProperty("validator.illegalargument.validatinglevel.part2"));
								throw new ValidatorException(
										language.getProperty("validator.illegalargument.validatinglevel.part1")
												+ validationLevel
												+ " "
												+ language
														.getProperty("validator.illegalargument.validatinglevel.part2"));
							}
						}
					}
				}
			}
		}
		// log violations
		if (violationList.size() > 0) {
			String violationListLogText = "";
			for (Violation violation : violationList) {
				violationListLogText = violationListLogText
						+ violation.getMessage() + System.lineSeparator();
			}
			LOGGER.info(language.getProperty("validator.logger.violationlist")
					+ System.lineSeparator() + violationListLogText);
		}
		return violationList;
	}

	/**
	 * This method puts all elements with an id found in the given document into
	 * a hash map. The key is the id, the value the element.
	 * 
	 * @param document
	 *            the document to get the elements from
	 * @return the hash map with elements reachable through their id
	 */
	private HashMap<String, Element> getAllElements(Document document) {
		HashMap<String, Element> elements = new HashMap<>();
		Element rootNode = document.getRootElement();
		Filter<Element> filter = Filters.element();
		IteratorIterable<Element> list = rootNode.getDescendants(filter);
		while (list.hasNext()) {
			Element element = list.next();
			Attribute id = element.getAttribute("id");
			// put the element if it has an id
			if (id != null) {
				String id_value = id.getValue();
				if (id_value != null && !id_value.equals("")) {
					elements.put(id_value, element);
				}
			}
		}

		return elements;
	}

	/**
	 * Creates a HashMap which uses the namespace-URI as an ID and another
	 * HashMap as value. The inner HashMap contains all Elements accessible via
	 * the ID as key {@see getAllElements()}
	 * 
	 * @param bpmnFiles
	 *            the files to be analyzed
	 * @return the grouped elements
	 */
	private HashMap<String, HashMap<String, Element>> getAllElementsGroupedByNamespace(
			List<Document> bpmnFiles) {
		HashMap<String, HashMap<String, Element>> groupedElements = new HashMap<>();

		for (Document doc : bpmnFiles) {
			String targetNamespace = doc.getRootElement().getAttributeValue(
					"targetNamespace");
			HashMap<String, Element> docElements = getAllElements(doc);

			if (groupedElements.containsKey(targetNamespace)) {
				HashMap<String, Element> previousElems = groupedElements
						.get(targetNamespace);
				previousElems.putAll(docElements);
			} else {
				groupedElements.put(targetNamespace, docElements);
			}
		}

		return groupedElements;

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
	private void validateReferenceType(HashMap<String, Element> elements,
			HashMap<String, HashMap<String, Element>> importedElements,
			List<Violation> violationList, Element currentElement, int line,
			Reference checkingReference, String referencedId, String ownPrefix) {
		if (checkingReference.isQname()) {
			// reference ID is prefixed and therefore probably to find in an
			// imported file
			if (referencedId.contains(":")) {
				String[] parts = referencedId.split(":");
				String prefix = parts[0];
				String importedId = parts[1];

				String namespace = "";

				for (Namespace nsp : currentElement.getNamespacesInScope()) {
					if (nsp.getPrefix().equals(prefix)) {
						namespace = nsp.getURI();
					}
				}
				HashMap<String, Element> relevantElements = importedElements
						.get(namespace);
				// case if the namespace is used for the root file and an
				// imported file
				if (ownPrefix.equals(prefix) && relevantElements != null) {
					// case if the element could not be found in the root file
					// and the imported file
					if (!elements.containsKey(importedId)
							&& !relevantElements.containsKey(importedId)) {
						createAndAddExistenceViolation(violationList, line,
								currentElement, checkingReference);
						// case if the element could be found in the root file
						// (and perhaps in the imported file, which does not
						// matter because of the same namespace)
					} else if (elements.containsKey(importedId)) {
						Element referencedElement = elements.get(importedId);
						checkTypeAndAddViolation(violationList, line,
								currentElement, checkingReference,
								referencedElement);
						// case if the element could be found only in the
						// imported file
					} else {
						Element referencedElement = relevantElements
								.get(importedId);
						checkTypeAndAddViolation(violationList, line,
								currentElement, checkingReference,
								referencedElement);
					}
					// case if the namespace is only used for the root file
				} else if (ownPrefix.equals(prefix)) {
					if (!elements.containsKey(importedId)) {
						createAndAddExistenceViolation(violationList, line,
								currentElement, checkingReference);
					} else {
						Element referencedElement = elements.get(importedId);
						checkTypeAndAddViolation(violationList, line,
								currentElement, checkingReference,
								referencedElement);
					}
					// case if the namespace is not used for the root file or an
					// imported file
				} else if (relevantElements == null) {
					// import does not exist or is no BPMN file (as it has to
					// be)
					createAndAddExistenceViolation(violationList, line,
							currentElement, checkingReference);

					// case if the namespace is used by an imported file
				} else {
					Element referencedElement = relevantElements
							.get(importedId);
					if (referencedElement == null) {
						createAndAddExistenceViolation(violationList, line,
								currentElement, checkingReference);
					} else {
						checkTypeAndAddViolation(violationList, line,
								currentElement, checkingReference,
								referencedElement);
					}
				}
				// the referenced element has no prefix and therefore is to find
				// in the root file
			} else {
				if (!elements.containsKey(referencedId)) {
					createAndAddExistenceViolation(violationList, line,
							currentElement, checkingReference);
				} else {
					Element referencedElement = elements.get(referencedId);
					checkTypeAndAddViolation(violationList, line,
							currentElement, checkingReference,
							referencedElement);
				}
			}
			// checking reference is IDREF (!QName) and therefore is to find in
			// the root file
		} else {
			if (!elements.containsKey(referencedId)) {
				createAndAddExistenceViolation(violationList, line,
						currentElement, checkingReference);
			} else {
				Element referencedElement = elements.get(referencedId);
				checkTypeAndAddViolation(violationList, line, currentElement,
						checkingReference, referencedElement);
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
	private void validateExistence(HashMap<String, Element> elements,
			HashMap<String, HashMap<String, Element>> importedElements,
			List<Violation> violationList, Element currentElement, int line,
			Reference checkingReference, String referencedId, String ownPrefix) {
		if (checkingReference.isQname()) {
			// reference ID is prefixed and therefore probably to find in an
			// imported file
			if (referencedId.contains(":")) {
				String[] parts = referencedId.split(":");
				String prefix = parts[0];
				String importedId = parts[1];
				HashMap<String, Element> relevantElements = importedElements
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
		ArrayList<String> referencedTypes = checkingReference.getTypes();
		// do not check references which are only for existence validation
		if (referencedTypes != null) {
			// find all possible types (with subtypes/children)
			@SuppressWarnings("unchecked")
			ArrayList<String> types = (ArrayList<String>) referencedTypes
					.clone();
			boolean childfound;
			do {
				childfound = false;
				@SuppressWarnings("unchecked")
				ArrayList<String> typesCopy = (ArrayList<String>) types.clone();
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
							ArrayList<String> tasks = getBPMNTasksList();
							ArrayList<String> subprocesses = getBPMNSubProcessList();
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
							ArrayList<String> tasks = getBPMNTasksList();
							ArrayList<String> subprocesses = getBPMNSubProcessList();
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
	private ArrayList<String> getBPMNTasksList() {
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
	private ArrayList<String> getBPMNSubProcessList() {
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
				getUriFromElement(currentElement), line,
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
			ArrayList<String> types) {

		String message = ViolationMessageCreator.createTypeViolationMessage(
				currentElement.getName(), line, checkingReference.getName(),
				referencedElement.getName(), types.toString(), language);

		Violation violation = new Violation(CONSTRAINT_REF_TYPE,
				getUriFromElement(currentElement), line,
				XPathHelper.getAbsolutePath(currentElement), message);

		violationList.add(violation);
	}

	private String getUriFromElement(Element element) {
		try {
			return element.getXMLBaseURI().toString();

		} catch (URISyntaxException e) {
			LOGGER.severe("Base URI of current element " + element.getName()
					+ " could not be restored.");
			return "unknown file";
		}
	}

}
