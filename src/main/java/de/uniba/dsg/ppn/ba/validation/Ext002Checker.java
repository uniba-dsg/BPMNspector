package de.uniba.dsg.ppn.ba.validation;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.bpmnspector.common.importer.BPMNProcess;
import de.uniba.dsg.ppn.ba.helper.ConstantHelper;
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
public class Ext002Checker {

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
                for(String key : idsOfChild.keySet()) {
                    if (nspIdMap.get(child.getNamespace()).containsKey(key)) {
                        // duplicate found create Violation:
                        createViolation(nspIdMap.get(child.getNamespace()).get(
                                key), idsOfChild.get(key), validationResult);
                    }
                }
            } else {
                nspIdMap.put(child.getNamespace(),idsOfChild);
            }
        }
    }

    private void createViolation(Attribute firstAttrib, Attribute secondAttrib,
            ValidationResult validationResult) {
        String file1Name = firstAttrib.getDocument().getBaseURI();
        int file1Line = ((LocatedElement) firstAttrib.getParent()).getLine();
        String file1XPath = XPathHelper.getAbsolutePath(firstAttrib);

        String file2Name = secondAttrib.getDocument().getBaseURI();
        int file2Line = ((LocatedElement) secondAttrib.getParent()).getLine();
        String file2XPath = XPathHelper.getAbsolutePath(secondAttrib);

        validationResult.getViolations().add(
                new Violation(CONSTRAINTNUMBER, file1Name,
                        file1Line,
                        file1XPath,
                        "Files have id duplicates"));
        validationResult.getViolations().add(
                new Violation(CONSTRAINTNUMBER, file2Name,
                        file2Line,
                        file2XPath,
                        "Files have id duplicates"));
        LOGGER.info("violation of constraint {} found.",
                CONSTRAINTNUMBER);
    }

    private Map<String, Attribute> getAllIdAttributesInProcess(BPMNProcess process) {
        Map<String, Attribute> map = new HashMap<>();
        String expStr = "//bpmn:*/@id";
        org.jdom2.xpath.XPathExpression<Attribute> exp = xPathFactory.compile(expStr, Filters.attribute(), null,
                Namespace.getNamespace("bpmn", ConstantHelper.BPMNNAMESPACE));
        List<Attribute> attributes = exp.evaluate(process.getProcessAsDoc());
        attributes.forEach(x -> map.put(x.getValue(), x));
        return map;
    }
}
