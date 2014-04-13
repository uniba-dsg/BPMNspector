package de.uniba.dsg.ppn.ba;

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

import org.jdom2.JDOMException;
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
		error.append(checkConstraint001(xmlFile, parentFolder));
		error.append(checkConstraint002(xmlFile, parentFolder));

		SchematronOutputType schematronOutputType = schematronSchema
				.applySchematronValidationToSVRL(new StreamSource(preProcessor
						.preProcess(headFileDocument, parentFolder)));
		for (int i = 0; i < schematronOutputType
				.getActivePatternAndFiredRuleAndFailedAssertCount(); i++) {
			if (schematronOutputType
					.getActivePatternAndFiredRuleAndFailedAssertAtIndex(i) instanceof FailedAssert) {
				FailedAssert failedAssert = (FailedAssert) schematronOutputType
						.getActivePatternAndFiredRuleAndFailedAssertAtIndex(i);
				String errorMessage = failedAssert.getText().substring(
						failedAssert.getText().indexOf('|') + 1);
				String constraint = failedAssert.getText().substring(0,
						failedAssert.getText().indexOf('|'));
				violations
						.add(new Violation(constraint, xmlFile.getName(),
								xmlLocator.findLine(xmlFile,
										failedAssert.getLocation()),
								failedAssert.getLocation(), errorMessage));
				error.append(failedAssert.getLocation() + ": " + errorMessage
						+ "\r\n");
			}
		}

		validationResult = new ValidationResult(error.toString().isEmpty(),
				checkedFiles, violations);
		return error.toString().isEmpty();
	}

	public ValidationResult getValidationResult() {
		return validationResult;
	}

	private String checkConstraint001(File headFile, File folder)
			throws ParserConfigurationException, SAXException, IOException,
			JDOMException {
		Document headFileDocument = documentBuilder.parse(headFile);
		Object[][] importedFiles = preProcessor.selectImportedFiles(
				headFileDocument, folder);

		boolean valid = true;
		for (int i = 0; i < importedFiles.length; i++) {
			if (!((File) importedFiles[i][0]).exists()) {
				valid = false;
				String xpathLocation = "//bpmn:import[@location = '"
						+ ((File) importedFiles[i][0]).getName() + "']";
				violations.add(new Violation("EXT.001",
						((File) importedFiles[i][0]).getName(), xmlLocator
								.findLine(headFile, xpathLocation),
						xpathLocation, "The imported file does not exist"));
			} else {
				String error = checkConstraint001(((File) importedFiles[i][0]),
						folder);
				if (!error.isEmpty()) {
					valid = false;
				}
			}
		}

		String message = valid ? "" : "One imported file does not exist";

		return message;
	}

	private String checkConstraint002(File headFile, File folder)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException, JDOMException {
		List<File> importedFileList = searchForImports(headFile, folder);

		boolean valid = true;
		for (int i = 0; i < importedFileList.size(); i++) {
			File file1 = importedFileList.get(i);
			Document document1 = documentBuilder.parse(file1);
			preProcessor.removeBPMNNode(document1);
			String namespace1 = document1.getDocumentElement().getAttribute(
					"targetNamespace");
			for (int j = i + 1; j < importedFileList.size(); j++) {
				File file2 = importedFileList.get(j);
				Document document2 = documentBuilder.parse(file2);
				preProcessor.removeBPMNNode(document2);
				String namespace2 = document2.getDocumentElement()
						.getAttribute("targetNamespace");
				if (namespace1.equals(namespace2)) {
					if (!checkNamespacesAndIdDuplicates(file1, file2,
							document1, document2)) {
						valid = false;
					}
				}
			}
		}

		String message = valid ? "" : "Files have id duplicates";

		return message;
	}

	private List<File> searchForImports(File file, File folder)
			throws SAXException, IOException {
		Document document = documentBuilder.parse(file);
		Object[][] importedFiles = preProcessor.selectImportedFiles(document,
				folder);
		List<File> importedFileList = new ArrayList<>();
		importedFileList.add(file);

		for (int i = 0; i < importedFiles.length; i++) {
			if (((File) importedFiles[i][0]).exists()) {
				checkedFiles.add(((File) importedFiles[i][0]).getName());
				importedFileList.addAll(searchForImports(
						((File) importedFiles[i][0]), folder));
			}
		}

		return importedFileList;
	}

	private boolean checkNamespacesAndIdDuplicates(File file1, File file2,
			Document document1, Document document2)
			throws XPathExpressionException, SAXException, IOException,
			JDOMException {
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
					String xpathLocation = "//bpmn:*[@id = '" + importedFile1Id
							+ "']";
					violations.add(new Violation("EXT.002", file1.getName(),
							xmlLocator.findLine(file1, xpathLocation),
							xpathLocation, "Files have id duplicates"));
					violations.add(new Violation("EXT.002", file2.getName(),
							xmlLocator.findLine(file2, xpathLocation),
							xpathLocation, "Files have id duplicates"));
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
