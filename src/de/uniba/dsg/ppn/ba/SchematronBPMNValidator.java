package de.uniba.dsg.ppn.ba;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
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

import org.oclc.purl.dsdl.svrl.FailedAssert;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import com.phloc.schematron.ISchematronResource;
import com.phloc.schematron.pure.SchematronResourcePure;

import de.uniba.dsg.bpmn.ValidationResult;
import de.uniba.dsg.bpmn.Violation;
import de.uniba.dsg.ppn.ba.helper.BpmnNamespaceContext;
import de.uniba.dsg.ppn.ba.helper.BpmnValidationException;
import de.uniba.dsg.ppn.ba.helper.ImportedFile;
import de.uniba.dsg.ppn.ba.helper.PreProcessResult;
import de.uniba.dsg.ppn.ba.helper.XmlWriter;

public class SchematronBPMNValidator implements BpmnValidator {

	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private XPathFactory xPathFactory;
	private XPath xpath;
	private XPathExpression xPathExpression;
	private PreProcessor preProcessor;
	private XmlLocator xmlLocator;
	private Logger logger;
	private XmlWriter xmlWriter;
	public final static String bpmnNamespace = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	final static String bpmndiNamespace = "http://www.omg.org/spec/BPMN/20100524/DI";

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
			xPathExpression = xpath.compile("//bpmn:*/@id");
		} catch (XPathExpressionException e) {
			// ignore
		}
		preProcessor = new PreProcessor();
		xmlLocator = new XmlLocator();
		xmlWriter = new XmlWriter();
		logger = (Logger) LoggerFactory.getLogger("BpmnValidator");
	}

	@Override
	public void setLogLevel(Level logLevel) {
		logger.setLevel(logLevel);
	}

	@Override
	public List<ValidationResult> validateFiles(List<File> xmlFiles)
			throws BpmnValidationException {
		List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
		for (File xmlFile : xmlFiles) {
			validationResults.add(validate(xmlFile));
		}
		return validationResults;
	}

	// TODO: refactor
	@Override
	public ValidationResult validate(File xmlFile)
			throws BpmnValidationException {
		final ISchematronResource schematronSchema = SchematronResourcePure
				.fromFile(SchematronBPMNValidator.class.getResource(
						"schematron/validation.xml").getPath());
		if (!schematronSchema.isValidSchematron()) {
			logger.error("schematron file is invalid");
			throw new BpmnValidationException("Invalid Schematron!");
		}

		logger.info("Validating {}", xmlFile.getName());

		ValidationResult validationResult = new ValidationResult();
		File parentFolder = xmlFile.getParentFile();

		try {
			Document headFileDocument = documentBuilder.parse(xmlFile);
			validationResult.getCheckedFiles().add(xmlFile.getAbsolutePath());

			checkConstraint001(xmlFile, parentFolder, validationResult);
			checkConstraint002(xmlFile, parentFolder, validationResult);

			List<String[]> namespaceTable = new ArrayList<>();
			PreProcessResult preProcessResult = preProcessor.preProcess(
					headFileDocument, parentFolder, namespaceTable);

			SchematronOutputType schematronOutputType = schematronSchema
					.applySchematronValidationToSVRL(new StreamSource(
							transformDocumentToInputStream(headFileDocument)));
			for (int i = 0; i < schematronOutputType
					.getActivePatternAndFiredRuleAndFailedAssertCount(); i++) {
				if (schematronOutputType
						.getActivePatternAndFiredRuleAndFailedAssertAtIndex(i) instanceof FailedAssert) {
					FailedAssert failedAssert = (FailedAssert) schematronOutputType
							.getActivePatternAndFiredRuleAndFailedAssertAtIndex(i);
					String message = failedAssert.getText().trim();
					String constraint = message.substring(0,
							message.indexOf('|'));
					String errorMessage = message.substring(message
							.indexOf('|') + 1);
					int line = xmlLocator.findLine(xmlFile,
							failedAssert.getLocation());
					String fileName = xmlFile.getName();
					String location = failedAssert.getLocation();
					logger.info(
							"violation of constraint {} in {} at {} found.",
							constraint, fileName, line);
					if (line == -1) {
						try {
							String xpathId = "";
							if (failedAssert.getDiagnosticReferenceCount() > 0) {
								xpathId = failedAssert.getDiagnosticReference()
										.get(0).getText().trim();
							}
							String[] result = searchForViolationFile(xpathId,
									validationResult,
									preProcessResult.getNamespaceTable());
							fileName = result[0];
							line = Integer.valueOf(result[1]);
							location = result[2];
						} catch (BpmnValidationException e) {
							fileName = e.getMessage();
						}
						logger.info(
								"preprocessing needed. violation in {} at {}.",
								fileName, line);
					}
					validationResult.getViolations().add(
							new Violation(constraint, fileName, line, location,
									errorMessage));
				}
			}

			for (int i = 0; i < validationResult.getCheckedFiles().size(); i++) {
				File f = new File(validationResult.getCheckedFiles().get(i));
				validationResult.getCheckedFiles().set(i, f.getName());
			}
		} catch (SAXException | IOException e) {
			logger.error("file {} couldn't be read. Cause: {}",
					xmlFile.getName(), e.getCause());
			throw new BpmnValidationException(
					"Given file couldn't be read or doesn't exist!");
		} catch (Exception e) {
			logger.error("exception. Cause: {}", e.getCause());
			throw new BpmnValidationException(
					"Something went wrong during schematron validation!");
		}

		validationResult.setValid(validationResult.getViolations().isEmpty());
		logger.info("Validating process successfully done, file is valid: {}",
				validationResult.isValid());

		try {
			xmlWriter.writeResult(validationResult, new File(parentFolder
					+ File.separator + "validation_result.xml"));
		} catch (JAXBException e) {
			logger.error("result couldn't be written in xml: {}",
					xmlFile.getName());
			throw new BpmnValidationException(
					"result couldn't be written in xml!");
		}

		return validationResult;
	}

	public ByteArrayInputStream transformDocumentToInputStream(
			Document headFileDocument) throws UnsupportedEncodingException,
			TransformerException {

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
			throws BpmnValidationException {
		boolean search = true;
		String fileName = "";
		String line = "-1";
		String xpathObjectId = "";
		int i = 0;
		while (search && i < validationResult.getCheckedFiles().size()) {
			File checkedFile = new File(validationResult.getCheckedFiles().get(
					i));
			try {
				Document document = documentBuilder.parse(checkedFile);
				String namespacePrefix = xpathExpression.substring(0,
						xpathExpression.indexOf('_'));
				String namespace = "";
				for (String[] s : namespaceTable) {
					if (s[0].equals(namespacePrefix)) {
						namespace = s[1];
					}
				}
				for (String checkedFilePath : validationResult
						.getCheckedFiles()) {
					checkedFile = new File(checkedFilePath);
					try {
						document = documentBuilder.parse(checkedFile);
						if (document.getDocumentElement()
								.getAttribute("targetNamespace")
								.equals(namespace)) {
							xpathObjectId = createIdBpmnExpression(xpathExpression
									.substring(xpathExpression.indexOf('_') + 1));
							line = ""
									+ xmlLocator.findLine(checkedFile,
											xpathObjectId);
							xpathObjectId += "[0]";
							fileName = checkedFile.getName();
							search = false;
							break;
						}
					} catch (SAXException | IOException e) {
						logger.error("file {} couldn't be read. Cause: {}",
								checkedFile.getName(), e.getCause());
					}
				}
			} catch (SAXException | IOException e) {
				logger.error("file {} couldn't be read. Cause: {}",
						checkedFile.getName(), e.getCause());
			}
			i++;
		}

		if (search) {
			throw new BpmnValidationException("BPMN Element couldn't be found!");
		}

		return new String[] { fileName, line, xpathObjectId };
	}

	private void checkConstraint001(File headFile, File folder,
			ValidationResult validationResult) {
		try {
			Document headFileDocument = documentBuilder.parse(headFile);

			ImportedFile[] importedFiles = preProcessor.selectImportedFiles(
					headFileDocument, folder, 0);

			for (int i = 0; i < importedFiles.length; i++) {
				if (!importedFiles[i].getFile().exists()) {
					String xpathLocation = "//bpmn:import[@location = '"
							+ importedFiles[i].getFile().getName() + "']";
					validationResult.getViolations().add(
							new Violation("EXT.001", importedFiles[i].getFile()
									.getName(), xmlLocator.findLine(headFile,
									xpathLocation), xpathLocation + "[0]",
									"The imported file does not exist"));
				} else {
					checkConstraint001(importedFiles[i].getFile(), folder,
							validationResult);
				}
			}
		} catch (SAXException | IOException e) {
			logger.error("file {} couldn't be read. Cause: {}",
					headFile.getName(), e.getCause());
		}
	}

	private void checkConstraint002(File headFile, File folder,
			ValidationResult validationResult) throws XPathExpressionException {
		List<File> importedFileList = searchForImports(headFile, folder,
				validationResult);

		for (int i = 0; i < importedFileList.size(); i++) {
			File file1 = importedFileList.get(i);
			try {
				Document document1 = documentBuilder.parse(file1);
				preProcessor.removeBPMNDINode(document1);
				String namespace1 = document1.getDocumentElement()
						.getAttribute("targetNamespace");
				for (int j = i + 1; j < importedFileList.size(); j++) {
					File file2 = importedFileList.get(j);
					try {
						Document document2 = documentBuilder.parse(file2);
						preProcessor.removeBPMNDINode(document2);
						String namespace2 = document2.getDocumentElement()
								.getAttribute("targetNamespace");
						if (namespace1.equals(namespace2)) {
							checkNamespacesAndIdDuplicates(file1, file2,
									document1, document2, validationResult);
						}
					} catch (IOException | SAXException e) {
						logger.error("file {} couldn't be read. Cause: {}",
								file2.getName(), e.getCause());
					}
				}
			} catch (IOException | SAXException e) {
				logger.error("file {} couldn't be read. Cause: {}",
						file1.getName(), e.getCause());
			}
		}
	}

	// TODO: refactor
	private List<File> searchForImports(File file, File folder,
			ValidationResult validationResult) {
		List<File> importedFileList = new ArrayList<>();
		try {
			Document document = documentBuilder.parse(file);
			ImportedFile[] importedFiles = preProcessor.selectImportedFiles(
					document, folder, 0);
			importedFileList.add(file);

			for (int i = 0; i < importedFiles.length; i++) {
				if (importedFiles[i].getFile().exists()) {
					validationResult.getCheckedFiles().add(
							importedFiles[i].getFile().getAbsolutePath());
					importedFileList.addAll(searchForImports(
							importedFiles[i].getFile(), folder,
							validationResult));
				}
			}
		} catch (IOException | SAXException e) {
			logger.error("file {} couldn't be read. Cause: {}", file.getName(),
					e.getCause());
		}

		return importedFileList;
	}

	private void checkNamespacesAndIdDuplicates(File file1, File file2,
			Document document1, Document document2,
			ValidationResult validationResult) throws XPathExpressionException {
		NodeList foundNodes1 = (NodeList) xPathExpression.evaluate(document1,
				XPathConstants.NODESET);
		NodeList foundNodes2 = (NodeList) xPathExpression.evaluate(document2,
				XPathConstants.NODESET);
		for (int k = 1; k < foundNodes1.getLength(); k++) {
			String importedFile1Id = foundNodes1.item(k).getNodeValue();
			for (int l = 1; l < foundNodes2.getLength(); l++) {
				String importedFile2Id = foundNodes2.item(l).getNodeValue();
				if (importedFile1Id.equals(importedFile2Id)) {
					String xpathLocation = createIdBpmnExpression(importedFile1Id);
					validationResult.getViolations().add(
							new Violation("EXT.002", file1.getName(),
									xmlLocator.findLine(file1, xpathLocation),
									xpathLocation + "[0]",
									"Files have id duplicates"));
					validationResult.getViolations().add(
							new Violation("EXT.002", file2.getName(),
									xmlLocator.findLine(file2, xpathLocation),
									xpathLocation + "[0]",
									"Files have id duplicates"));
				}
			}
		}
	}

	private String createIdBpmnExpression(String id) {
		return "//bpmn:*[@id = '" + id + "']";
	}

}
