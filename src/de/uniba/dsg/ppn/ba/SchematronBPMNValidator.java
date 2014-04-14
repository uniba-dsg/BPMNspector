package de.uniba.dsg.ppn.ba;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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

	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private XPathFactory xPathFactory;
	private XPath xpath;
	private XPathExpression xPathExpression;
	private PreProcessor preProcessor;
	private XmlLocator xmlLocator;
	static String bpmnNamespace = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	static String bpmndiNamespace = "http://www.omg.org/spec/BPMN/20100524/DI";

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
		xpath.setNamespaceContext(new BpmnNamespaceContext());
		try {
			xPathExpression = xpath.compile("//*/@id");
		} catch (XPathExpressionException e) {
			// ignore
		}
		preProcessor = new PreProcessor();
		xmlLocator = new XmlLocator();
	}

	// TODO: refactor
	public ValidationResult validate(File xmlFile) throws Exception {
		final ISchematronResource schematronSchema = SchematronResourcePure
				.fromFile(SchematronBPMNValidator.class.getResource(
						"schematron/validation.xml").getPath());
		if (!schematronSchema.isValidSchematron()) {
			throw new IllegalArgumentException("Invalid Schematron!");
		}

		Document headFileDocument = documentBuilder.parse(xmlFile);
		File parentFolder = xmlFile.getParentFile();
		ValidationResult validationResult = new ValidationResult();
		validationResult.getCheckedFiles().add(xmlFile.getAbsolutePath());

		checkConstraint001(xmlFile, parentFolder, validationResult);
		checkConstraint002(xmlFile, parentFolder, validationResult);

		List<String[]> namespaceTable = new ArrayList<>();
		PreProcessResult preProcessResult = preProcessor.preProcess(
				headFileDocument, parentFolder, namespaceTable);

		Helper.printDocument(preProcessResult.getDocumentResult());

		SchematronOutputType schematronOutputType = schematronSchema
				.applySchematronValidationToSVRL(new StreamSource(
						transformDocumentToInputStream(headFileDocument)));
		for (int i = 0; i < schematronOutputType
				.getActivePatternAndFiredRuleAndFailedAssertCount(); i++) {
			if (schematronOutputType
					.getActivePatternAndFiredRuleAndFailedAssertAtIndex(i) instanceof FailedAssert) {
				FailedAssert failedAssert = (FailedAssert) schematronOutputType
						.getActivePatternAndFiredRuleAndFailedAssertAtIndex(i);
				String message = failedAssert.getText();
				String xpathExpr = message.substring(0, message.indexOf('~'));
				message = message.substring(message.indexOf('~') + 1);
				String constraint = message.substring(0, message.indexOf('|'));
				String errorMessage = message
						.substring(message.indexOf('|') + 1);
				int line = xmlLocator.findLine(xmlFile,
						failedAssert.getLocation());
				String fileName = xmlFile.getName();
				if (line == -1) {
					String[] result = searchForViolationFile(xpathExpr,
							validationResult,
							preProcessResult.getNamespaceTable());
					fileName = result[0];
					line = Integer.valueOf(result[1]);
				}
				validationResult.getViolations().add(
						new Violation(constraint, fileName, line, failedAssert
								.getLocation(), errorMessage));
			}
		}

		return validationResult;
	}

	public ByteArrayInputStream transformDocumentToInputStream(
			Document headFileDocument) throws SAXException, IOException,
			XPathExpressionException, TransformerException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer
				.transform(new DOMSource(headFileDocument), new StreamResult(
						new OutputStreamWriter(outputStream, "UTF-8")));

		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				outputStream.toByteArray());

		return inputStream;
	}

	// TODO: refactor
	private String[] searchForViolationFile(String xpathExpression,
			ValidationResult validationResult, List<String[]> namespaceTable)
			throws SAXException, IOException, XPathExpressionException,
			JDOMException {
		boolean search = true;
		String fileName = "";
		String line = "-1";
		int i = 0;
		System.out.println(xpathExpression);
		while (search && i < validationResult.getCheckedFiles().size()) {
			File f = new File(validationResult.getCheckedFiles().get(i));
			Document d = documentBuilder.parse(f);
			String namespacePrefix = xpathExpression.substring(0,
					xpathExpression.indexOf('_'));
			String namespace = "";
			for (String[] s : namespaceTable) {
				if (s[0].equals(namespacePrefix)) {
					namespace = s[1];
				}
			}
			for (String s : validationResult.getCheckedFiles()) {
				f = new File(s);
				d = documentBuilder.parse(f);
				if (d.getDocumentElement().getAttribute("targetNamespace")
						.equals(namespace)) {
					line = ""
							+ xmlLocator.findLine(
									f,
									"//bpmn:*[@id ='"
											+ xpathExpression
													.substring(xpathExpression
															.indexOf('_') + 1)
											+ "']");
					fileName = f.getName();
					search = false;
					break;
				}

			}
			i++;
		}

		return new String[] { fileName, line };
	}

	// TODO: refactor
	private String checkConstraint001(File headFile, File folder,
			ValidationResult validationResult)
			throws ParserConfigurationException, SAXException, IOException,
			JDOMException {
		Document headFileDocument = documentBuilder.parse(headFile);
		Object[][] importedFiles = preProcessor.selectImportedFiles(
				headFileDocument, folder, 0);

		boolean valid = true;
		for (int i = 0; i < importedFiles.length; i++) {
			if (!((File) importedFiles[i][0]).exists()) {
				valid = false;
				String xpathLocation = "//bpmn:import[@location = '"
						+ ((File) importedFiles[i][0]).getName() + "']";
				validationResult.getViolations().add(
						new Violation("EXT.001", ((File) importedFiles[i][0])
								.getName(), xmlLocator.findLine(headFile,
								xpathLocation), xpathLocation,
								"The imported file does not exist"));
			} else {
				String error = checkConstraint001(((File) importedFiles[i][0]),
						folder, validationResult);
				if (!error.isEmpty()) {
					valid = false;
				}
			}
		}

		String message = valid ? "" : "One imported file does not exist";

		return message;
	}

	// TODO: refactor
	private String checkConstraint002(File headFile, File folder,
			ValidationResult validationResult)
			throws ParserConfigurationException, SAXException, IOException,
			XPathExpressionException, JDOMException {
		List<File> importedFileList = searchForImports(headFile, folder,
				validationResult);

		boolean valid = true;
		for (int i = 0; i < importedFileList.size(); i++) {
			File file1 = importedFileList.get(i);
			Document document1 = documentBuilder.parse(file1);
			preProcessor.removeBPMNDINode(document1);
			String namespace1 = document1.getDocumentElement().getAttribute(
					"targetNamespace");
			for (int j = i + 1; j < importedFileList.size(); j++) {
				File file2 = importedFileList.get(j);
				Document document2 = documentBuilder.parse(file2);
				preProcessor.removeBPMNDINode(document2);
				String namespace2 = document2.getDocumentElement()
						.getAttribute("targetNamespace");
				if (namespace1.equals(namespace2)) {
					if (!checkNamespacesAndIdDuplicates(file1, file2,
							document1, document2, validationResult)) {
						valid = false;
					}
				}
			}
		}

		String message = valid ? "" : "Files have id duplicates";

		return message;
	}

	// TODO: refactor
	private List<File> searchForImports(File file, File folder,
			ValidationResult validationResult) throws SAXException, IOException {
		Document document = documentBuilder.parse(file);
		Object[][] importedFiles = preProcessor.selectImportedFiles(document,
				folder, 0);
		List<File> importedFileList = new ArrayList<>();
		importedFileList.add(file);

		for (int i = 0; i < importedFiles.length; i++) {
			if (((File) importedFiles[i][0]).exists()) {
				validationResult.getCheckedFiles().add(
						((File) importedFiles[i][0]).getAbsolutePath());
				importedFileList
						.addAll(searchForImports(((File) importedFiles[i][0]),
								folder, validationResult));
			}
		}

		return importedFileList;
	}

	// TODO: refactor
	private boolean checkNamespacesAndIdDuplicates(File file1, File file2,
			Document document1, Document document2,
			ValidationResult validationResult) throws XPathExpressionException,
			SAXException, IOException, JDOMException {
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
					validationResult.getViolations().add(
							new Violation("EXT.002", file1.getName(),
									xmlLocator.findLine(file1, xpathLocation),
									xpathLocation, "Files have id duplicates"));
					validationResult.getViolations().add(
							new Violation("EXT.002", file2.getName(),
									xmlLocator.findLine(file2, xpathLocation),
									xpathLocation, "Files have id duplicates"));
					valid = false;
				}
			}
		}

		return valid;
	}

}
