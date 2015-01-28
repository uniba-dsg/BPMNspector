package de.uniba.dsg.bpmnspector.common.importer;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.ValidatorException;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.bpmnspector.common.xsdvalidation.BpmnXsdValidator;
import de.uniba.dsg.bpmnspector.common.xsdvalidation.WsdlValidator;
import de.uniba.dsg.ppn.ba.helper.ConstantHelper;
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
            throws ValidatorException {
        return importProcessRecursively(path, null, null, result);
    }

    private BPMNProcess importProcessRecursively(Path path, BPMNProcess parent, BPMNProcess rootProcess, ValidationResult result)
            throws ValidatorException {
        result.getCheckedFiles().add(path.toString());
        if(Files.notExists(path) || !Files.isRegularFile(path)) {
            String msg = "Import could not be resolved: Path "+path+" is invalid.";
            Violation violation = createViolation(parent, path, msg);
            result.getViolations().add(violation);
            result.setValid(false);

            throw new ValidatorException(msg);
        } else {
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



            } catch (ValidatorException e) {
                // Thrown if file is not well-formed - error is already logged and
                // added to the validation result - but further processing is not
                // possible

                return null;
            } catch (SAXException | JDOMException | IOException e) {
                throw new ValidatorException("Creation of BPMNProcess object failed.", e);
            }
        }
    }

    private void resolveAndAddImports(BPMNProcess process, BPMNProcess rootProcess, ValidationResult result)
            throws ValidatorException {

        List<Element> importElements = process.getProcessAsDoc().getRootElement().getChildren("import", getBPMNNamespace());

        for (Element elem : importElements) {
            String importType = elem.getAttributeValue("importType");
            Path importPath = Paths.get(process.getBaseURI()).getParent().resolve(elem.getAttributeValue("location")).normalize().toAbsolutePath();
            if (ConstantHelper.BPMNNAMESPACE.equals(importType)) {
                if(!isFileAlreadyImported(importPath.toString(), rootProcess)) {
                    BPMNProcess importedProcess = importProcessRecursively(
                            importPath, process, rootProcess, result);
                    if(importedProcess!=null) {
                        process.getChildren().add(importedProcess);
                    }
                }
            } else if(ConstantHelper.WSDL2NAMESPACE.equals(importType)) {
                try {
                    wsdlValidator.validateAgainstXsd(importPath.toFile(), result);
                    process.getWsdls().add(builder.build(importPath.toFile()));
                } catch (SAXParseException e) {
                    String msg = "File "+importPath+" is not a valid WSDL file.";
                    Violation violation = createViolation(process, importPath, msg);
                    result.getViolations().add(violation);
                    result.setValid(false);
                } catch (IOException | SAXException | JDOMException e) {
                    throw new ValidatorException("WSDL validation of file "+importPath+" failed.", e);
                }
            } else if(ConstantHelper.XSDNAMESPACE.equals(importType)) {
                SchemaFactory schemaFactory = SchemaFactory
                        .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                try {
                    schemaFactory
                            .newSchema(importPath.toFile());
                } catch (SAXException e) {
                    String msg = "File "+importPath+" is not a valid XSD file.";
                    Violation violation = createViolation(process, importPath, msg);
                    result.getViolations().add(violation);
                    result.setValid(false);
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

    private Violation createViolation(BPMNProcess parent, Path path, String msg) {
        int line = -1;
        String xpath = "";

        List<Element> imports = parent.getProcessAsDoc().getRootElement().getChildren("import",
                getBPMNNamespace());
        for(Element elem : imports) {
            Path importPath = Paths.get(parent.getBaseURI()).getParent().resolve(elem.getAttributeValue("location")).normalize().toAbsolutePath();
            if(importPath.equals(path)) {
                line = ((LocatedElement) elem).getLine();
                xpath = XPathHelper.getAbsolutePath(elem);
            }
        }

        return new Violation("EXT.001", parent.getBaseURI(), line, xpath, msg);
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
