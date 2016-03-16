package de.uniba.dsg.bpmnspector.common.importer;

import api.*;
import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import de.uniba.dsg.bpmnspector.common.xsdvalidation.BpmnXsdValidator;
import de.uniba.dsg.bpmnspector.common.xsdvalidation.WsdlValidator;
import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedElement;
import org.jdom2.located.LocatedJDOMFactory;
import org.jdom2.xpath.XPathHelper;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Matthias Geiger
 * @version 1.0
 */
public class ProcessImporter {

    private final SAXBuilder builder;
    private final BpmnXsdValidator bpmnXsdValidator;
    private final WsdlValidator wsdlValidator;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ProcessImporter.class.getSimpleName());

    public ProcessImporter() {
        builder = new SAXBuilder();
        builder.setJDOMFactory(new LocatedJDOMFactory());
        bpmnXsdValidator = new BpmnXsdValidator();
        wsdlValidator = new WsdlValidator();
    }

    public BPMNProcess importProcessFromPath(Path path, ValidationResult result)
            throws ValidationException {
        if(Files.notExists(path) || !Files.isRegularFile(path)) {
            String msg = "BPMNProcess cannot be created: Path "+path+" is invalid.";
            throw new ValidationException(msg);
        }
        Resource resource = new Resource(path);
        return importProcessRecursively(resource, null, null, result);
    }

    public BPMNProcess importProcessFromStreamSource(InputStream src, String resourceName, ValidationResult result)
            throws ValidationException {
        try {
            Resource resource = new Resource(resourceName);
            byte[] streamContent = IOUtils.toByteArray(src);
            bpmnXsdValidator
                    .validateAgainstXsd(new ByteArrayInputStream(streamContent), resource, result);
            Document processAsDoc = builder.build(new ByteArrayInputStream(streamContent), resource.getResourceName());
            if ("definitions".equals(processAsDoc.getRootElement().getName()) && ConstantHelper.BPMNNAMESPACE
                    .equals(processAsDoc.getRootElement().getNamespaceURI())) {
                String processNamespace = processAsDoc.getRootElement().getAttributeValue("targetNamespace");

                BPMNProcess process = new BPMNProcess(processAsDoc, resourceName, processNamespace, null);

                // remove BPMNDI information
                processAsDoc.getRootElement().removeChildren("BPMNDiagram", getBPMNDINamespace());

                return process;
            } else {
                // Invalid BPMN file
                return null;
            }
        } catch (ValidationException e) {
            // Thrown if file is not well-formed or does not have claimed encoding- error is already logged and
            // added to the validation result - but further processing is not
            // possible

            return null;
        } catch (SAXException | JDOMException | IOException e) {
            throw new ValidationException("Creation of BPMNProcess for stream (" + resourceName + ") object failed.", e);
        }
    }

    private BPMNProcess importProcessRecursively(Resource resource, BPMNProcess parent, BPMNProcess rootProcess, ValidationResult result)
            throws ValidationException {
        result.addResource(resource);
        try (InputStream stream = openStreamToResource(resource)) {
            try {
                byte[] streamContent = IOUtils.toByteArray(stream);
                bpmnXsdValidator.validateAgainstXsd(new ByteArrayInputStream(streamContent), resource, result);
                Document processAsDoc = builder.build(new ByteArrayInputStream(streamContent), resource.getResourceName());
                if ("definitions".equals(processAsDoc.getRootElement().getName()) && ConstantHelper.BPMNNAMESPACE
                        .equals(processAsDoc.getRootElement().getNamespaceURI())) {
                    String processNamespace = processAsDoc.getRootElement().getAttributeValue("targetNamespace");

                    BPMNProcess process = new BPMNProcess(processAsDoc, resource.getResourceName(), processNamespace, parent);

                    // remove BPMNDI information
                    processAsDoc.getRootElement().removeChildren("BPMNDiagram", getBPMNDINamespace());

                    if (rootProcess == null) {
                        resolveAndAddImports(process, process, result);
                    } else {
                        resolveAndAddImports(process, rootProcess, result);
                    }
                    return process;
                } else {
                    // Invalid BPMN file
                    return null;
                }
            } catch (ValidationException e) {
                // Thrown if file is not well-formed or does not have claimed encoding- error is already logged and
                // added to the validation result - but further processing is not
                // possible
                LOGGER.debug("caught: "+e);
                return null;
            } catch (SAXException | JDOMException | IOException e) {
                throw new ValidationException("Creation of BPMNProcess for file " + resource.getResourceName() + " object failed.",
                        e);
            }
        } catch (IOException e) {
            throw new ValidationException("Failure accessing BPMN process "+resource.getResourceName()+".", e);
        }
    }

    private void resolveAndAddImports(BPMNProcess process, BPMNProcess rootProcess, ValidationResult result)
            throws ValidationException {

        List<Element> importElements = process.getProcessAsDoc().getRootElement().getChildren("import", getBPMNNamespace());

        for (Element elem : importElements) {
            String importType = elem.getAttributeValue("importType");

            // fail fast if import type is not supported
            if (!(ConstantHelper.BPMNNAMESPACE.equals(importType) || ConstantHelper.WSDL2NAMESPACE.equals(importType)
                    || ConstantHelper.XSDNAMESPACE.equals(importType))) {

                int line = ((LocatedElement) elem).getLine();
                int column = ((LocatedElement) elem).getColumn();
                String xpath = XPathHelper.getAbsolutePath(elem);
                Location loc = new Location(Paths.get(process.getBaseURI()), new LocationCoordinate(line, column),
                        xpath);
                result.addWarning(new Warning("The import type '"+importType+"' is not supported. Import will be ignored.", loc));
                return;
            }

            String location = elem.getAttributeValue("location");

            Resource resource = null;
            // determine whether a absolute URL or a file is used and create corresponding Resource
            try {

                URI importUri = new URI(location);

                if(importUri.isAbsolute() && importUri.getScheme().toLowerCase().startsWith("http")) {
                    // process as URL
                    URL asURL = importUri.toURL();
                    resource = new Resource(asURL);
                } else {
                    // process as file
                    String decodedUrlString = URLDecoder.decode(importUri.toString(), "UTF-8");
                    Path importPath = Paths.get(decodedUrlString);
                    if (!importPath.isAbsolute()) {
                        // resolve relative path based on the baseURI from the process
                        importPath = Paths.get(process.getBaseURI()).getParent().resolve(importPath).normalize().toAbsolutePath();
                    }

                    if (Files.notExists(importPath) || !Files.isRegularFile(importPath)) {
                        String msg = "Import could not be resolved: Path " + elem.getAttributeValue("location") + " is invalid.";
                        Violation violation = createViolation(process, elem, msg);
                        result.addViolation(violation);
                    } else {
                        resource = new Resource(importPath);
                    }
                }
            } catch (URISyntaxException | MalformedURLException e ) {
                String msg = "Import could not be resolved: Path " + location + " is invalid.";
                Violation violation = createViolation(process, elem, msg);
                result.addViolation(violation);
            } catch (UnsupportedEncodingException e) {
                throw new ValidationException("URI could not be decoded correctly.", e);
            }

            if (resource != null) {
                if (ConstantHelper.BPMNNAMESPACE.equals(importType)) {
                    if (!isFileAlreadyImported(resource.getResourceName(), rootProcess)) {
                        try {
                            BPMNProcess importedProcess = importProcessRecursively(resource, process, rootProcess,
                                    result);

                            if (importedProcess != null) {
                                process.getChildren().add(importedProcess);
                            }
                        } catch (ValidationException e) {
                            result.addViolation(createViolation(process, elem, e.getMessage()));
                        }
                    }
                } else if (ConstantHelper.WSDL2NAMESPACE.equals(importType)) {
                    try (InputStream stream = openStreamToResource(resource)) {

                        result.addResource(resource);

                        wsdlValidator.validateAgainstXsd(stream, resource, result);
                        process.getWsdls().add(builder.build(stream));
                    } catch (ValidationException e) {
                        // Creation of stream failed object could not be found
                        result.addViolation(createViolation(process, elem, e.getMessage()));
                    } catch (SAXParseException e) {
                        String msg = "File " + resource.getResourceName() + " is not a valid WSDL file.";
                        result.addViolation(createViolation(process, elem, msg));
                    } catch (IOException | SAXException | JDOMException e) {
                        throw new ValidationException(
                                "WSDL validation of file " + resource.getResourceName() + " failed.", e);
                    }
                } else if (ConstantHelper.XSDNAMESPACE.equals(importType)) {
                    try (InputStream stream = openStreamToResource(resource)) {
                        result.addResource(resource);
                        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                        schemaFactory.newSchema(new StreamSource(stream));
                    } catch (ValidationException e) {
                        // Creation of stream failed object could not be found
                        result.addViolation(createViolation(process, elem, e.getMessage()));
                    } catch (SAXException e) {
                        String msg = "File " + resource.getResourceName() + " is not a valid XSD file.";
                        result.addViolation(createViolation(process, elem, msg));
                    } catch (IOException e) {
                        throw new ValidationException("XSD file check for  " + resource.getResourceName() + " failed.",
                                e);
                    }
                }
            }
        }
    }

    private Namespace getBPMNNamespace() {
        return Namespace.getNamespace(ConstantHelper.BPMNNAMESPACE);
    }

    private Namespace getBPMNDINamespace() {
        return Namespace.getNamespace(ConstantHelper.BPMNDINAMESPACE);
    }

    private Violation createViolation(BPMNProcess parent, Element importElement, String msg) {
        int line = ((LocatedElement) importElement).getLine();
        int column = ((LocatedElement) importElement).getColumn();
        String xpath = XPathHelper.getAbsolutePath(importElement);

        Location location = new Location(Paths.get(parent.getBaseURI()), new LocationCoordinate(line, column), xpath);
        return new Violation(location, msg, "EXT.001");
    }

    private boolean isFileAlreadyImported(String baseURI, BPMNProcess process) {
        if(process.getBaseURI().equals(baseURI)) {
            return true;
        } else {
            for(BPMNProcess child : process.getChildren()) {
                if(isFileAlreadyImported(baseURI, child)) {
                    return true;
                }
            }
        }
        return false;
    }

    private InputStream openStreamToResource(Resource resource)
            throws ValidationException, IOException {

        if (resource.getType() == Resource.ResourceType.URL) {
            LOGGER.debug("Trying to openStream to: "+resource.getResourceName());
            try {
                return resource.getUrl().get().openConnection().getInputStream();
            } catch (UnknownHostException e) {
                throw new ValidationException("Host "+e.getMessage()+" is unknown.", e);
            } catch (FileNotFoundException e) {
                throw new ValidationException("File cannot be resolved from URL: "+e.getMessage(), e);
            }
        } else if (resource.getType() == Resource.ResourceType.FILE) {
            return new FileInputStream(resource.getPath().get().toFile());
        } else {
            throw new IllegalArgumentException(
                    "Import processing of resource type " + resource.getType() + " is not supported.");
        }
    }

}
