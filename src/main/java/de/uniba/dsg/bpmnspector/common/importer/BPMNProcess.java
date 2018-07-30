package de.uniba.dsg.bpmnspector.common.importer;

import api.Location;
import api.LocationCoordinate;
import api.ValidationException;
import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.located.LocatedElement;
import org.jdom2.util.IteratorIterable;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class BPMNProcess {

    private final Document processAsDoc;

    private final String baseURI;
    private final String namespace;
    private final String generatedPrefix;

    private final BPMNProcess parent;

    private final List<BPMNProcess> children = new ArrayList<>();

    private final List<Document> wsdls = new ArrayList<>();
    private final List<Document> xsds = new ArrayList<>();

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(BPMNProcess.class.getSimpleName());
    private static final Namespace BPMN_NAMESPACE = Namespace.getNamespace("bpmn", ConstantHelper.BPMN_NAMESPACE);

    public BPMNProcess(Document processAsDoc, String baseURI, String namespace) {
        this(processAsDoc, baseURI, namespace, null);
    }

    public BPMNProcess(Document processAsDoc, String baseURI, String namespace,
                       BPMNProcess parent) {
        this.processAsDoc = processAsDoc;
        this.baseURI = baseURI;
        this.namespace = namespace;
        this.parent = parent;
        this.generatedPrefix = createPrefixForProcess();
    }

    public List<BPMNProcess> getChildren() {
        return children;
    }

    public Document getProcessAsDoc() {
        return processAsDoc;
    }

    public String getBaseURI() {
        return baseURI;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getGeneratedPrefix() {
        return generatedPrefix;
    }

    public BPMNProcess getParent() {
        return parent;
    }

    public List<Document> getWsdls() {
        return wsdls;
    }

    public List<Document> getXsds() {
        return xsds;
    }

    public void getAllProcessesRecursively(List<BPMNProcess> processList) {
        if (!processList.contains(this)) {
            processList.add(this);
            for (BPMNProcess child : this.getChildren()) {
                child.getAllProcessesRecursively(processList);
            }
        }
    }

    public List<BPMNProcess> findProcessByNamespace(String namespace) {
        List<BPMNProcess> allProcesses = new ArrayList<>();
        getAllProcessesRecursively(allProcesses);
        return allProcesses.stream().filter(
                process -> process.getNamespace().equals(namespace)).collect(
                Collectors.toList());
    }

    public Optional<BPMNProcess> findProcessByGeneratedPrefix(String prefix) {
        List<BPMNProcess> allProcesses = new ArrayList<>();
        getAllProcessesRecursively(allProcesses);
        return allProcesses.stream().filter(
                process -> process.getGeneratedPrefix().equals(prefix)).findFirst();
    }

    private String createPrefixForProcess() {

        return "ns" + (this.namespace + this.baseURI).hashCode();
    }

    public Optional<Element> findElementById(String id) {
        IteratorIterable<Element> it = processAsDoc.getRootElement().getDescendants(Filters.element());
        while (it.hasNext()) {
            Element nextElem = it.next();
            Attribute nextId = nextElem.getAttribute("id");
            if (nextId != null && id.equals(nextId.getValue())) {
                return Optional.of(nextElem);
            }
        }

        return Optional.empty();
    }

    public Location determineLocationByXPath(String xpathExpression)
            throws ValidationException {

        LOGGER.debug("Found ID:" + xpathExpression);

        int line = -1;
        int column = -1;

        // use ID with generated prefix for lookup
        String xpathObjectId = createIdBpmnExpression(xpathExpression);

        LOGGER.debug("Expression to evaluate: " + xpathObjectId);
        XPathFactory fac = XPathFactory.instance();
        List<Element> elems = fac.compile(xpathObjectId, Filters.element(), null,
                BPMN_NAMESPACE).evaluate(processAsDoc);

        if (elems.size() == 1) {
            line = ((LocatedElement) elems.get(0)).getLine();
            column = ((LocatedElement) elems.get(0)).getColumn();
            //use ID without prefix  (=original ID) as Violation xPath
            xpathObjectId = createIdBpmnExpression(xpathExpression.substring(xpathExpression.indexOf('_') + 1));
        }

        if (line == -1 || column == -1) {
            throw new ValidationException("BPMN Element couldn't be found in file '" + baseURI + "'!");
        }

        return new Location(Paths.get(baseURI).toAbsolutePath(), new LocationCoordinate(line, column),
                xpathObjectId);
    }

    public boolean hasConditionalSeqFlowTasks() {
        IteratorIterable<Element> it = processAsDoc.getRootElement().getDescendants(Filters.element("sequenceFlow", BPMN_NAMESPACE));
        while (it.hasNext()) {
            Element seqFlow = it.next();
            if (seqFlow.getChild("conditionExpression", BPMN_NAMESPACE) != null) {
                Attribute sourceRefAttribute = seqFlow.getAttribute("sourceRef");
                if (sourceRefAttribute != null) {
                    String sourceId = sourceRefAttribute.getValue();
                    Optional<Element> elem = findElementById(sourceId);
                    if (elem.isPresent()) {
                        String elemName = elem.get().getName();
                        if (!("exclusiveGateway".equals(elemName) || "parallelGateway".equals(elemName) ||
                                "inclusiveGateway".equals(elemName) || "complexGateway".equals(elemName))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean hasBoundaryEvents() {
        IteratorIterable<Element> it = processAsDoc.getRootElement().getDescendants(Filters.element("boundaryEvent", BPMN_NAMESPACE));
        return it.hasNext();
    }

    /**
     * Method to determine whether the BPMN process contains more than one process definition
     *
     * @return true iff more than one participant or process is defined in the process document
     */
    public boolean isMultiProcessModel() {

        // Case 1: Multiple processes within one BPMN file
        List<Element> processElems = processAsDoc.getRootElement().getChildren("process", BPMN_NAMESPACE);
        if(processElems.size() > 1) {
            return true;
        }

        // case 2: only one process, but collaboration contains two or more participants (--> blackbox pools)
        Element collaboration = processAsDoc.getRootElement().getChild("collaboration", BPMN_NAMESPACE);
        if(collaboration != null) {
            List<Element> participants = collaboration.getChildren("participant", BPMN_NAMESPACE);
            return participants.size() > 1;
        }

        return false;
    }

    /**
     * creates an xpath expression for finding the id
     *
     * @param id the id, to which the expression should refer
     * @return the xpath expression, which refers the given id
     */
    private static String createIdBpmnExpression(String id) {
        return String.format("//bpmn:*[@id = '%s']", id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BPMNProcess that = (BPMNProcess) o;
        System.out.println(processAsDoc.toString());
        return Objects.equals(baseURI, that.baseURI) &&
                Objects.equals(namespace, that.namespace) &&
                processAsDoc.toString().equals(that.processAsDoc.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseURI, namespace);
    }
}
