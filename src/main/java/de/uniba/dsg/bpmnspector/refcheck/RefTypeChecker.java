package de.uniba.dsg.bpmnspector.refcheck;

import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.bpmnspector.refcheck.utils.JDOMUtils;
import de.uniba.dsg.bpmnspector.refcheck.utils.ViolationMessageCreator;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.xpath.XPathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class RefTypeChecker {

    public static final String CONSTRAINT_REF_TYPE = "REF_TYPE";

    private Properties language;

    private final ExistenceChecker existenceChecker;

    private final Map<String, BPMNElement> bpmnRefElements;

    public RefTypeChecker(Properties language, ExistenceChecker existenceChecker, Map<String, BPMNElement> bpmnRefElements) {
        this.language = language;
        this.existenceChecker = existenceChecker;
        this.bpmnRefElements = bpmnRefElements;
    }

    /**
     * Setter to change Language after initial creation.
     *
     * @param language
     *            - the new language properties to use
     */
    public void setLanguage(Properties language) {
        this.language = language;
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
    public void validateReferenceType(Map<String, Element> elements,
            Map<String, Map<String, Element>> importedElements,
            List<Violation> violationList, Element currentElement, int line,
            Reference checkingReference, String referencedId, String ownPrefix) {
        if (checkingReference.isQname()) {

            // Check whether the QName is prefixed
            if (referencedId.contains(":")) { // reference ID is prefixed and
                // therefore probably to find in
                // an
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

                // Checking whether the namespace is used for the root and/or
                // the imported file
                // to determine the correct element to be checked
                if (ownPrefix.equals(prefix)) { // namespace is used for the
                    // root file

                    // check whether element with ID importedId exists in root
                    // or another file
                    if (elements.containsKey(importedId)) { // elem is in root
                        // file
                        checkTypeAndAddViolation(violationList, line,
                                currentElement, checkingReference,
                                elements.get(importedId));
                    } else if (relevantImportedElements != null
                            && relevantImportedElements.containsKey(importedId)) { // element
                        // is
                        // found
                        // in
                        // the
                        // imported
                        // file
                        checkTypeAndAddViolation(violationList, line,
                                currentElement, checkingReference,
                                relevantImportedElements.get(importedId));
                    } else {
                        existenceChecker.createAndAddExistenceViolation(
                                violationList, line,
                                currentElement, checkingReference);
                    }

                } else if (relevantImportedElements != null) { // NOPMD
                    // namespace is used by an imported file
                    if (relevantImportedElements.containsKey(importedId)) {
                        checkTypeAndAddViolation(violationList, line,
                                currentElement, checkingReference,
                                relevantImportedElements.get(importedId));
                    } else {
                        existenceChecker.createAndAddExistenceViolation(
                                violationList, line,
                                currentElement, checkingReference);
                    }
                } else {
                    // invalid namespace: not used in any file
                    // case if the namespace is not used for the root file or an
                    // imported file
                    // import does not exist or is no BPMN file (as it has to
                    // be)
                    existenceChecker.createAndAddExistenceViolation(
                            violationList, line,
                            currentElement, checkingReference);
                }

            } else { // no QName prefix - the elem has to be in the root file
                if (elements.containsKey(referencedId)) {
                    checkTypeAndAddViolation(violationList, line,
                            currentElement, checkingReference,
                            elements.get(referencedId));
                } else {
                    existenceChecker.createAndAddExistenceViolation(
                            violationList, line,
                            currentElement, checkingReference);
                }
            }

        } else { // reference uses IDREF - ref has to exist in root file
            if (elements.containsKey(referencedId)) {
                checkTypeAndAddViolation(violationList, line, currentElement,
                        checkingReference, elements.get(referencedId));
            } else {
                existenceChecker.createAndAddExistenceViolation(violationList,
                        line,
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
        boolean validType = false;
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
                    validType = true;
                    break;
                }
            }

            if (!validType
                    || checkingReference.isSpecial() && !performSpecialChecks(
                    currentElement, checkingReference,
                    referencedElement)) {
                createAndAddReferenceTypeViolation(violationList, line,
                        currentElement, checkingReference, referencedElement,
                        types);
            }
        }
    }

    /**
     * Method to perform additional checks for special cases
     *
     * @param currentElement
     *            Element to check
     * @param checkingReference
     *            reference to check
     * @param referencedElement
     *            referenced element
     * @return true if no violation is found, false otherwise
     */
    private boolean performSpecialChecks(Element currentElement,
            Reference checkingReference, Element referencedElement) {
        // perform additional checks for special cases (look up bachelor thesis
        // for the reference number)
        boolean validType = true;
        if (checkingReference.getNumber() == 18
                || checkingReference.getNumber() == 19) {
            String isEventSubprocess = referencedElement
                    .getAttributeValue("triggeredByEvent");
            if (isEventSubprocess != null && isEventSubprocess.equals("true")) {
                validType = false;
            }
        } else if (checkingReference.getNumber() == 62) {
            Element parent = currentElement.getParentElement();
            if (parent != null) {
                List<String> tasks = getBPMNTasksList();
                List<String> subprocesses = getBPMNSubProcessList();
                if (tasks.contains(parent.getName())
                        && !referencedElement.getName().equals("dataInput")) {
                    validType = false;
                } else if (subprocesses.contains(parent.getName())
                        && !referencedElement.getName().equals("dataObject")) {
                    validType = false;
                }
            }
        } else if (checkingReference.getNumber() == 63) {
            Element parent = currentElement.getParentElement();
            if (parent != null) {
                List<String> tasks = getBPMNTasksList();
                List<String> subprocesses = getBPMNSubProcessList();
                if (tasks.contains(parent.getName()) &&
                        !referencedElement.getName().equals("dataOutput")) {
                    validType = false;
                }
                else if (subprocesses.contains(parent.getName()) &&
                        !referencedElement.getName().equals("dataObject")) {
                    validType = false;
                }
            }
        }
        return validType;
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
