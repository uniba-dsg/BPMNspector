package de.uniba.dsg.bpmnspector.common.importer;

import api.*;
import de.uniba.dsg.bpmnspector.common.util.ConstantHelper;
import de.uniba.dsg.bpmnspector.common.xsdvalidation.BpmnXsdValidator;
import de.uniba.dsg.bpmnspector.common.xsdvalidation.WsdlValidator;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedElement;
import org.jdom2.located.LocatedJDOMFactory;
import org.jdom2.xpath.XPathHelper;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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
        return importProcessRecursively(path, null, null, result);
    }

    private BPMNProcess importProcessRecursively(Path path, BPMNProcess parent, BPMNProcess rootProcess, ValidationResult result)
            throws ValidationException {
        result.addFile(path);
        try {
            bpmnXsdValidator.validateAgainstXsd(path.toFile(), result);
            Document processAsDoc = builder.build(path.toFile());
            if("definitions".equals(processAsDoc.getRootElement().getName()) && ConstantHelper.BPMNNAMESPACE.equals(processAsDoc.getRootElement().getNamespaceURI())) {
                String processNamespace = processAsDoc.getRootElement()
                        .getAttributeValue("targetNamespace");

                BPMNProcess process = new BPMNProcess(processAsDoc,
                        path.toString(), processNamespace, parent);

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

            return null;
        } catch (SAXException | JDOMException | IOException e) {
            throw new ValidationException("Creation of BPMNProcess for file " + path.toString() + " object failed.", e);
        }
    }

    private void resolveAndAddImports(BPMNProcess process, BPMNProcess rootProcess, ValidationResult result)
            throws ValidationException {

        List<Element> importElements = process.getProcessAsDoc().getRootElement().getChildren("import", getBPMNNamespace());

        for (Element elem : importElements) {
            String importType = elem.getAttributeValue("importType");
            try {
                Path importPath = Paths.get(elem.getAttributeValue("location"));
                if(!importPath.isAbsolute()) {
                    // resolve relative path based on the baseURI from the process
                    importPath = Paths.get(process.getBaseURI()).getParent().resolve(importPath).normalize().toAbsolutePath();
                }

                if(Files.notExists(importPath) || !Files.isRegularFile(importPath)) {
                    String msg = "Import could not be resolved: Path "+elem.getAttributeValue("location")+" is invalid.";
                    Violation violation = createViolation(process, elem, msg);
                    result.addViolation(violation);
                } else {
                    if (ConstantHelper.BPMNNAMESPACE.equals(importType)) {
                        if (!isFileAlreadyImported(importPath.toString(), rootProcess)) {
                            BPMNProcess importedProcess = importProcessRecursively(
                                    importPath, process, rootProcess, result);
                            if (importedProcess != null) {
                                process.getChildren().add(importedProcess);
                            }
                        }
                    } else if (ConstantHelper.WSDL2NAMESPACE.equals(importType)) {
                        result.addFile(importPath);
                        try {
                            wsdlValidator.validateAgainstXsd(importPath.toFile(), result);
                            process.getWsdls().add(builder.build(importPath.toFile()));
                        } catch (SAXParseException e) {
                            String msg = "File " + importPath + " is not a valid WSDL file.";
                            result.addViolation(createViolation(process, elem, msg));
                        } catch (IOException | SAXException | JDOMException e) {
                            throw new ValidationException("WSDL validation of file " + importPath + " failed.", e);
                        }
                    } else if (ConstantHelper.XSDNAMESPACE.equals(importType)) {
                        result.addFile(importPath);
                        SchemaFactory schemaFactory = SchemaFactory
                                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                        try {
                            schemaFactory
                                    .newSchema(importPath.toFile());
                        } catch (SAXException e) {
                            String msg = "File " + importPath + " is not a valid XSD file.";
                            result.addViolation(createViolation(process, elem, msg));
                        }
                    }
                }
            } catch(InvalidPathException e) {
                result.addViolation(createViolation(process, elem, "Import location " + elem.getAttributeValue("location")+" is not a valid Path"));
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

}
