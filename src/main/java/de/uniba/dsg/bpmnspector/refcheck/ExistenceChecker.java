package de.uniba.dsg.bpmnspector.refcheck;

import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.bpmnspector.refcheck.utils.JDOMUtils;
import de.uniba.dsg.bpmnspector.refcheck.utils.ViolationMessageCreator;
import org.jdom2.Element;
import org.jdom2.xpath.XPathHelper;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ExistenceChecker {

    public static final String CONSTRAINT_REF_EXISTENCE = "REF_EXISTENCE";

    private Properties language;

    public ExistenceChecker(Properties language) {
        this.language = language;
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
    public void validateExistence(Map<String, Element> elements,
            Map<String, Map<String, Element>> importedElements,
            List<Violation> violationList, Element currentElement, int line,
            Reference checkingReference, String referencedId, String ownPrefix) {
        boolean foundViolation = false;
        if (checkingReference.isQname()) {
            // reference ID is prefixed and therefore probably to find in an
            // imported file
            if (referencedId.contains(":")) {
                String[] parts = referencedId.split(":");
                String prefix = parts[0];
                String importedId = parts[1];

                Map<String, Element> relevantImportedElements = importedElements
                        .get(prefix);

                if (ownPrefix.equals(prefix)
                        && relevantImportedElements != null // namespace is used
                        // for the root
                        && !elements.containsKey(importedId) // file and an
                        // imported file
                        && !relevantImportedElements.containsKey(importedId)) {
                    foundViolation = true;

                } else if (ownPrefix.equals(prefix) && // namespace is used for
                        // the root file only
                        !elements.containsKey(importedId)) {
                    foundViolation = true;

                } else if (relevantImportedElements != null && // NOPMD //
                        // namespace is
                        // used by an
                        // imported file
                        !relevantImportedElements.containsKey(importedId)) {
                    foundViolation = true;

                } else { // invalid namespace: not used in any file
                    // import does not exist or is no BPMN file (as it has to
                    // be)
                    foundViolation = true;
                }

            } else { // no QName prefix - the elem has to be in the root file
                if (!elements.containsKey(referencedId)) {
                    foundViolation = true;
                }
            }

        } else { // reference uses IDREF - ref has to exist in root file
            if (!elements.containsKey(referencedId)) {
                foundViolation = true;
            }
        }

        if (foundViolation) {
            createAndAddExistenceViolation(violationList, line, currentElement,
                    checkingReference);
        }
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
    public void createAndAddExistenceViolation(List<Violation> violationList,
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
}
