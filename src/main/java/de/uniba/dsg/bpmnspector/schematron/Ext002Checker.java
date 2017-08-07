package de.uniba.dsg.bpmnspector.schematron;

import api.*;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import de.uniba.dsg.bpmnspector.common.util.ResourceUtils;
import org.jdom2.Attribute;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.located.LocatedElement;
import org.jdom2.xpath.XPathFactory;
import org.jdom2.xpath.XPathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is resposible for the check of the EXT.002 constraint
 *
 * @author Philipp Neugebauer
 * @author Matthias Geiger
 * @version 1.0
 *
 */
class Ext002Checker {

    private final static Logger LOGGER = LoggerFactory.getLogger(Ext002Checker.class.getSimpleName());
    private static final String CONSTRAINTNUMBER = "EXT.002";

    private final XPathFactory xPathFactory = XPathFactory.instance();

    /**
     * checks, if there are violations of the EXT.002 constraint
     *
     * @param process
     *                  the process to be checked
     * @param validationResult
     *            the current validation result of validating process for adding
     *            found violations
     */
    public void checkConstraint002(BPMNProcess process, ValidationResult validationResult) {

        Map<String, Map<String, Attribute>> nspIdMap = new HashMap<>();

        ArrayList<BPMNProcess> allChildren = new ArrayList<>();
        process.getAllProcessesRecursively(allChildren);

        // Add IDs of all processes
        for(BPMNProcess child : allChildren) {
            Map<String, Attribute> idsOfChild = getAllIdAttributesInProcess(child);
            if(nspIdMap.containsKey(child.getNamespace())) {
                // duplicate found create Violation:
                idsOfChild.keySet().stream()
                        .filter(key -> nspIdMap.get(child.getNamespace()).containsKey(key))
                        .forEach(key -> createViolation(nspIdMap.get(child.getNamespace())
                                .get(key), idsOfChild.get(key), validationResult));
            } else {
                nspIdMap.put(child.getNamespace(),idsOfChild);
            }
        }
    }

    private void createViolation(Attribute firstAttrib, Attribute secondAttrib,
            ValidationResult validationResult) {
        try {
            Resource firstResource = ResourceUtils
                    .determineAndCreateResourceFromString(firstAttrib.getDocument().getBaseURI(), null);

            int file1Line = ((LocatedElement) firstAttrib.getParent()).getLine();
            int file1Column = ((LocatedElement) secondAttrib.getParent()).getColumn();
            String file1XPath = XPathHelper.getAbsolutePath(firstAttrib);

            Resource secondResource = ResourceUtils
                    .determineAndCreateResourceFromString(secondAttrib.getDocument().getBaseURI(), null);
            int file2Line = ((LocatedElement) secondAttrib.getParent()).getLine();
            int file2Column = ((LocatedElement) secondAttrib.getParent()).getColumn();
            String file2XPath = XPathHelper.getAbsolutePath(secondAttrib);

            Location location = new Location(firstResource, new LocationCoordinate(file1Line, file1Column), file1XPath);
            Violation violation = new Violation(location, "Resources have id duplicates", CONSTRAINTNUMBER);
            validationResult.addViolation(violation);

            Location location2 = new Location(secondResource, new LocationCoordinate(file2Line, file2Column), file2XPath);
            Violation violation2 = new Violation(location2, "Resources have id duplicates", CONSTRAINTNUMBER);
            validationResult.addViolation(violation2);

            LOGGER.info("violation of constraint {} found.", CONSTRAINTNUMBER);
        } catch (ValidationException e) {
            throw new IllegalArgumentException("Invalid baseURI detected.", e);
        }
    }

    private Map<String, Attribute> getAllIdAttributesInProcess(BPMNProcess process) {
        Map<String, Attribute> map = new HashMap<>();
        String expStr = "//bpmn:*/@id";
        org.jdom2.xpath.XPathExpression<Attribute> exp = xPathFactory.compile(expStr, Filters.attribute(), null,
                Namespace.getNamespace("bpmn", ConstantHelper.BPMN_NAMESPACE));
        List<Attribute> attributes = exp.evaluate(process.getProcessAsDoc());
        attributes.forEach(x -> map.put(x.getValue(), x));
        return map;
    }
}
