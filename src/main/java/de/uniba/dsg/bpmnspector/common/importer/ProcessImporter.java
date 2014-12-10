package de.uniba.dsg.bpmnspector.common.importer;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.Violation;
import de.uniba.dsg.bpmnspector.common.xsdvalidation.BpmnXsdValidator;
import de.uniba.dsg.bpmnspector.refcheck.ValidatorException;
import de.uniba.dsg.ppn.ba.helper.ConstantHelper;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ProcessImporter {

    private final SAXBuilder builder;
    private final BpmnXsdValidator bpmnXsdValidator;

    public ProcessImporter() {
        builder = new SAXBuilder();
        builder.setJDOMFactory(new LocatedJDOMFactory());
        bpmnXsdValidator = new BpmnXsdValidator();
    }

    public BPMNProcess importProcessFromPath(Path path, ValidationResult result)
            throws ValidatorException {
        return importProcessRecursively(path, null, null, result);
    }

    private BPMNProcess importProcessRecursively(Path path, BPMNProcess parent, BPMNProcess rootProcess, ValidationResult result)
            throws ValidatorException {
        if(Files.notExists(path) || !Files.isRegularFile(path)) {
            throw new ValidatorException("Import could not be resolved: Path "+path+"is invalid.");
        } else {
            try {
                bpmnXsdValidator.validateAgainstXsd(path.toFile(), result);
                Document processAsDoc = builder.build(path.toFile());
                if("definitions".equals(processAsDoc.getRootElement().getName()) && ConstantHelper.BPMNNAMESPACE.equals(processAsDoc.getRootElement().getNamespaceURI())) {
                    String processNamespace = processAsDoc.getRootElement()
                            .getAttributeValue("targetNamespace");

                    BPMNProcess process = new BPMNProcess(processAsDoc,
                            path.toString(), processNamespace, parent);
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

        List<Element> importElements = process.getProcessAsDoc().getRootElement().getChildren("import", getBPMNNamespace(process.getProcessAsDoc()));

        for (Element elem : importElements) {
            String importType = elem.getAttributeValue("importType");
            if (ConstantHelper.BPMNNAMESPACE.equals(importType)) {
                Path importPath = Paths.get(process.getBaseURI()).getParent().resolve(elem.getAttributeValue("location")).normalize().toAbsolutePath();

                if(!isFileAlreadyImported(importPath.toString(), rootProcess)) {
                    BPMNProcess importedProcess = importProcessRecursively(
                            importPath, process, rootProcess, result);
                    if(importedProcess!=null) {
                        process.getChildren().add(importedProcess);
                    }
                }
            }
        }
    }

    private Namespace getBPMNNamespace(Document doc) {
        for(Namespace nsp : doc.getNamespacesInScope()) {
            if(nsp.getURI().equals(ConstantHelper.BPMNNAMESPACE)) {
                return nsp;
            }
        }
        return null;
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
