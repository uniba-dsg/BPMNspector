package de.uniba.dsg.bpmnspector.common.util;

import api.ValidationResult;
import api.Violation;
import api.Warning;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;


/**
 * This class is used for writing xml files via jaxb
 *
 * @author Philipp Neugebauer
 * @author Matthias Geiger
 * @version 1.0
 *
 */
public class XmlWriterApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlWriterApi.class
            .getSimpleName());

    private final JDOMFactory factory = new DefaultJDOMFactory();
    private final Namespace nsp = Namespace.getNamespace(ConstantHelper.PINAMESPACE);

    public static Path createXmlReport(ValidationResult result) {
        try {
            String fileName = result.getFoundFiles().get(0).getFileName().toString();
            Path reportPath = FileUtils.createResourcesForReports();

            Path reportFile = FileUtils.createFileForReport(reportPath, fileName, "xml");

            XmlWriterApi xmlWriter = new XmlWriterApi();
            xmlWriter.writeResult(result, reportFile);

            return reportFile;
        } catch ( IOException ioe) {
            LOGGER.error("Creation of XML Report files failed.", ioe);
            return null;
        }
    }

    /**
     * writes the result to the given file
     *
     * @param result
     *            the validation result, which should be written to a file
     * @param file
     *            the file, where the validation result should be written to
     * @throws java.io.IOException
     *             if an error occurs during xml writing process
     */
    private void writeResult(ValidationResult result, Path file) throws IOException {
        Document doc = createDocFromValidationResult(result);
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        outputter.output(doc, new FileOutputStream(file.toFile()));
        LOGGER.info("Report for file {} successfully written to {}", result.getFoundFiles().get(0).getFileName().toString(), file.toString());
    }

    private Document createDocFromValidationResult(ValidationResult result) {

        Element root = factory.element("validationResult", nsp);


        root.addContent(createTextElement("valid", String.valueOf(result.isValid())));

        Element checkedFiles = factory.element("checkedFiles", nsp);
        result.getFoundFiles().forEach(x -> checkedFiles.addContent(createTextElement("file", x.toString())));
        root.addContent(checkedFiles);

        if(!result.isValid()) {
            Element filesWithViolations = factory.element("filesWithViolations", nsp);
            result.getFilesWithViolations().forEach(x -> filesWithViolations.addContent(createTextElement("file", x.toString())));
            root.addContent(filesWithViolations);

            Element violations = factory.element("violations", nsp);
            result.getViolations().forEach(x -> violations.addContent(createViolationElement(x)));
            root.addContent(violations);
        }
        if(result.getWarnings()!=null && !result.getWarnings().isEmpty()) {
            Element warnings = factory.element("warnings", nsp);
            result.getWarnings().forEach(x -> warnings.addContent(createWarningElement(x)));
            root.addContent(warnings);
        }

        return factory.document(root);
    }

    private Element createViolationElement(Violation violation) {
        Element vElem = factory.element("violation", nsp);
        vElem.setAttribute("constraint", violation.getConstraint());
        vElem.setAttribute("fileName", violation.getLocation().getResourceName());
        if(violation.getLocation().getLocation().getRow()!=-1) {
            vElem.setAttribute("line", String.valueOf(violation.getLocation().getLocation().getRow()));
        }
        if(violation.getLocation().getLocation().getColumn()!=-1) {
            vElem.setAttribute("column", String.valueOf(violation.getLocation().getLocation().getColumn()));
        }
        if(violation.getLocation().getXpath().isPresent()) {
            vElem.addContent(createTextElement("xPath", violation.getLocation().getXpath().get()));
        }
        vElem.addContent(createTextElement("message", violation.getMessage()));
        return vElem;
    }

    private Element createWarningElement(Warning warning) {
        Element wElem = factory.element("warning", nsp);
        wElem.setAttribute("fileName", warning.getLocation().getResourceName());
        if(warning.getLocation().getLocation().getRow()!=-1) {
            wElem.setAttribute("line", String.valueOf(warning.getLocation().getLocation().getRow()));
        }
        if(warning.getLocation().getLocation().getColumn()!=-1) {
            wElem.setAttribute("column", String.valueOf(warning.getLocation().getLocation().getColumn()));
        }
        if(warning.getLocation().getXpath().isPresent()) {
            wElem.addContent(createTextElement("xPath", warning.getLocation().getXpath().get()));
        }
        wElem.addContent(createTextElement("message", warning.getMessage()));
        return wElem;
    }


    private Element createTextElement(String name, String text) {
        Element element = factory.element(name, nsp);
        element.setText(text);
        return element;
    }
}