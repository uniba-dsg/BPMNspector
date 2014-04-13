package de.uniba.dsg.ppn.ba;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.pure.SchematronResourcePure;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;

public class SchematronBPMNValidator {

	private StringBuffer error;
	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private XPathFactory xPathFactory;
	private XPath xpath;
	private XPathExpression xPathExpression;
	private PreProcessor preProcessor;
	private ValidationResult validationResult;
	private List<String> checkedFiles;
	private List<Violation> violations;
	private XmlLocator xmlLocator;
	static String bpmnNamespace = "http://www.omg.org/spec/BPMN/20100524/MODEL";

	{
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// ignore
		}
		xPathFactory = XPathFactory.newInstance();
		xpath = xPathFactory.newXPath();
		try {
			xPathExpression = xpath.compile("//*/@id");
		} catch (XPathExpressionException e) {
			// ignore
		}
		preProcessor = new PreProcessor();
		xmlLocator = new XmlLocator();
	}

	// TODO: REFACTOR!!
	public boolean validate(File xmlFile) throws Exception {
		final ISchematronResource schematronSchema = SchematronResourcePure
				.fromFile(SchematronBPMNValidator.class.getResource(
						"schematron/validation.xml").getPath());
		if (!schematronSchema.isValidSchematron()) {
			throw new IllegalArgumentException("Invalid Schematron!");
		}

		Document headFileDocument = documentBuilder.parse(xmlFile);
		File parentFolder = xmlFile.getParentFile();
		checkedFiles = new ArrayList<>();
		checkedFiles.add(xmlFile.getName());
		violations = new ArrayList<>();

		error = new StringBuffer();
		error.append(checkConstraint001(headFileDocument, parentFolder));
		error.append(checkConstraint002(headFileDocument, parentFolder));

		String xmlString = preProcessor.preProcess(headFileDocument,
				parentFolder);

		SchematronOutputType schematronOutputType = schematronSchema
				.applySchematronValidationToSVRL(new StreamSource(
						new ByteArrayInputStream(xmlString.getBytes("UTF-8"))));
		for (int i = 0; i < schematronOutputType
				.getActivePatternAndFiredRuleAndFailedAssertCount(); i++) {
			if (schematronOutputType
					.getActivePatternAndFiredRuleAndFailedAssertAtIndex(i) instanceof FailedAssert) {
				FailedAssert failedAssert = (FailedAssert) schematronOutputType
						.getActivePatternAndFiredRuleAndFailedAssertAtIndex(i);
				violations.add(new Violation("", xmlFile.getName(), xmlLocator
						.findLine(xmlFile, failedAssert.getLocation()),
						failedAssert.getLocation(), failedAssert.getText()));
				error.append(failedAssert.getLocation() + ": "
						+ failedAssert.getText() + "\r\n");
			}
		}

		validationResult = new ValidationResult(error.toString().isEmpty(),
				checkedFiles, new ArrayList<Violation>());
		return error.toString().isEmpty();
	}

	public ValidationResult getValidationResult() {
		return validationResult;
	}

	private String checkConstraint001(Document headFileDocument, File folder)
			throws ParserConfigurationException, SAXException, IOException {
		Object[][] importedFiles = preProcessor.selectImportedFiles(
				headFileDocument, folder);

		boolean valid = true;
		for (int i = 0; i < importedFiles.length; i++) {
			if (!((File) importedFiles[i][0]).exists()) {
				valid = false;
			} else {
				Document importedDocument = documentBuilder
						.parse((File) importedFiles[i][0]);
				String error = checkConstraint001(importedDocument, folder);
				if (!error.isEmpty()) {
					valid = false;
				}
			}
		}

		String message = valid ? "" : "One imported file does not exist";

		return message;
	}

	private String checkConstraint002(Document headFileDocument, File folder)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException {
		List<Document> importedFileList = searchForImports(headFileDocument,
				folder);

		boolean valid = true;
		for (int i = 0; i < importedFileList.size(); i++) {
			Document document1 = importedFileList.get(i);
			String namespace1 = document1.getDocumentElement().getAttribute(
					"targetNamespace");
			for (int j = i + 1; j < importedFileList.size(); j++) {
				Document document2 = importedFileList.get(j);
				String namespace2 = document2.getDocumentElement()
						.getAttribute("targetNamespace");
				if (namespace1.equals(namespace2)) {
					if (!checkNamespacesAndIdDuplicates(document1, document2)) {
						valid = false;
					}
				}
			}
		}

		String message = valid ? "" : "Files have double ids";

		return message;
	}

	private List<Document> searchForImports(Document document, File folder)
			throws SAXException, IOException {
		Object[][] importedFiles = preProcessor.selectImportedFiles(document,
				folder);
		List<Document> importedFileList = new ArrayList<>();
		importedFileList.add(document);

		for (int i = 0; i < importedFiles.length; i++) {
			if (((File) importedFiles[i][0]).exists()) {
				checkedFiles.add(((File) importedFiles[i][0]).getName());
				Document importedDocument = documentBuilder
						.parse((File) importedFiles[i][0]);
				importedFileList.addAll(searchForImports(importedDocument,
						folder));
			}
		}

		return importedFileList;
	}

	private boolean checkNamespacesAndIdDuplicates(Document document1,
			Document document2) throws XPathExpressionException, SAXException,
			IOException {
		NodeList foundNodes1 = (NodeList) xPathExpression.evaluate(document1,
				XPathConstants.NODESET);
		NodeList foundNodes2 = (NodeList) xPathExpression.evaluate(document2,
				XPathConstants.NODESET);
		boolean valid = true;
		for (int k = 1; k < foundNodes1.getLength(); k++) {
			String importedFile1Id = foundNodes1.item(k).getNodeValue();
			for (int l = 1; l < foundNodes2.getLength(); l++) {
				String importedFile2Id = foundNodes2.item(l).getNodeValue();
				if (importedFile1Id.equals(importedFile2Id)) {
					valid = false;
				}
			}
		}

		return valid;
	}

	public String getErrors() {
		return error.toString().trim();
	}

}
