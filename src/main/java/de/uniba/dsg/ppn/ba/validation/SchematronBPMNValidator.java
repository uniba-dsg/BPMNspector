package de.uniba.dsg.ppn.ba.validation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
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
import de.uniba.dsg.ppn.ba.preprocessing.ImportedFile;
import de.uniba.dsg.ppn.ba.preprocessing.PreProcessResult;
import de.uniba.dsg.ppn.ba.preprocessing.PreProcessor;

/**
 * Implementation of BpmnValidator
 * 
 * More information : {@link BpmnValidator}
 * 
 * Does the validation process of the xsd and the schematron validation and
 * returns the results of the validation
 * 
 * @author Philipp Neugebauer
 * @version 1.0
 * 
 */
public class SchematronBPMNValidator implements BpmnValidator {

	private DocumentBuilderFactory documentBuilderFactory;
	private DocumentBuilder documentBuilder;
	private XPathFactory xPathFactory;
	private XPath xpath;
	private XPathExpression xPathExpression;
	private PreProcessor preProcessor;
	private XmlLocator xmlLocator;
	private XsdValidator xsdValidator;
	private Logger logger;
	public final static String bpmnNamespace = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	public final static String bpmndiNamespace = "http://www.omg.org/spec/BPMN/20100524/DI";

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
		xsdValidator = new XsdValidator();
		logger = (Logger) LoggerFactory.getLogger(getClass().getSimpleName());
	}

	@Override
	public Level getLogLevel() {
		return logger.getLevel();
	}

	@Override
	public void setLogLevel(Level logLevel) {
		logger.setLevel(logLevel);
		setClassLogLevel(preProcessor, logLevel);
		setClassLogLevel(xmlLocator, logLevel);
		setClassLogLevel(xsdValidator, logLevel);
	}

	private void setClassLogLevel(Object classObject, Level logLevel) {
		((Logger) LoggerFactory.getLogger(classObject.getClass()
				.getSimpleName())).setLevel(logLevel);
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
				.fromClassPath("validation.xml");
		if (!schematronSchema.isValidSchematron()) {
			logger.error("schematron file is invalid");
			throw new BpmnValidationException("Invalid Schematron!");
		}

		logger.info("Validating {}", xmlFile.getName());

		ValidationResult validationResult = new ValidationResult();

		try {
			Document headFileDocument = documentBuilder.parse(xmlFile);
			validationResult.getCheckedFiles().add(xmlFile.getAbsolutePath());
			File parentFolder = xmlFile.getParentFile();

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
					logger.info("violation of constraint {} in {} found.",
							constraint, fileName);
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
						} catch (StringIndexOutOfBoundsException e) {
							fileName = "Element couldn't be found!";
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
					xmlFile.getName(), e);
			throw new BpmnValidationException(
					"Given file couldn't be read or doesn't exist!");
		} catch (Exception e) {
			logger.error("exception at schematron validation. Cause: {}",
					e);
			throw new BpmnValidationException(
					"Something went wrong during schematron validation!");
		}

		validationResult.setValid(validationResult.getViolations().isEmpty());
		logger.info("Validating process successfully done, file is valid: {}",
				validationResult.isValid());

		return validationResult;
	}

	/**
	 * transforms the given headFileDocument to an inputstream, so that it can
	 * be used for the schematron validation process
	 * 
	 * @param headFileDocument
	 * @return input stream with the head file document
	 * @throws UnsupportedEncodingException
	 *             if the encoding isn't supported
	 * @throws TransformerException
	 *             if anything fails during transformation process
	 */
	private ByteArrayInputStream transformDocumentToInputStream(
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
	/**
	 * searches for the file and line, where the violation occured
	 * 
	 * @param xpathExpression
	 *            the expression, through which the file and line should be
	 *            identified
	 * @param validationResult
	 *            for getting all checked files
	 * @param namespaceTable
	 *            to identify the file, where the violation occured
	 * @return string array with filename, line and xpath expression to find the
	 *         element
	 * @throws BpmnValidationException
	 *             if no element can be found
	 */
	private String[] searchForViolationFile(String xpathExpression,
			ValidationResult validationResult, List<String[]> namespaceTable)
			throws BpmnValidationException {
		String fileName = "";
		String line = "-1";
		String xpathObjectId = "";
		for (int i = 0; i < validationResult.getCheckedFiles().size()
				&& line.equals("-1"); i++) {
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
							line = String.valueOf(xmlLocator.findLine(
									checkedFile, xpathObjectId));
							xpathObjectId += "[0]";
							fileName = checkedFile.getName();
							break;
						}
					} catch (SAXException | IOException e) {
						logger.error("file {} couldn't be read. Cause: {}",
								checkedFile.getName(), e);
					}
				}
			} catch (SAXException | IOException e) {
				logger.error("file {} couldn't be read. Cause: {}",
						checkedFile.getName(), e);
			}
		}

		if (line.equals("-1")) {
			throw new BpmnValidationException("BPMN Element couldn't be found!");
		}

		return new String[] { fileName, line, xpathObjectId };
	}

	/**
	 * checks, if there are violations of the EXT.001 constraint
	 * 
	 * @param headFile
	 *            the file which should be checked
	 * @param folder
	 *            the parent folder of the file
	 * @param validationResult
	 *            the current validation result of validating process for adding
	 *            found violations
	 */
	private void checkConstraint001(File headFile, File folder,
			ValidationResult validationResult) {
		try {
			xsdValidator.validateAgainstXsd(headFile, validationResult);
			Document headFileDocument = documentBuilder.parse(headFile);

			ImportedFile[] importedFiles = preProcessor.selectImportedFiles(
					headFileDocument, folder, 0);

			String constraint = "EXT.001";
			for (int i = 0; i < importedFiles.length; i++) {
				if (!importedFiles[i].getFile().exists()) {
					String xpathLocation = "//bpmn:import[@location = '"
							+ importedFiles[i].getFile().getName() + "']";
					String fileName = importedFiles[i].getFile().getName();
					int line = xmlLocator.findLine(headFile, xpathLocation);
					validationResult.getViolations().add(
							new Violation(constraint, fileName, line,
									xpathLocation + "[0]",
									"The imported file does not exist"));
					logger.info("violation of constraint {} in {} found.",
							constraint, fileName);
				} else {
					checkConstraint001(importedFiles[i].getFile(), folder,
							validationResult);
				}
			}
		} catch (SAXException | IOException e) {
			logger.error("file {} couldn't be read. Cause: {}",
					headFile.getName(), e);
		}
	}

	/**
	 * checks, if there are violations of the EXT.002 constraint
	 * 
	 * @param headFile
	 *            the file which should be checked
	 * @param folder
	 *            the parent folder of the file
	 * @param validationResult
	 *            the current validation result of validating process for adding
	 *            found violations
	 * @throws XPathExpressionException
	 *             if there's an invalid xpath expression used
	 */
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
								file2.getName(), e);
					}
				}
			} catch (IOException | SAXException e) {
				logger.error("file {} couldn't be read. Cause: {}",
						file1.getName(), e);
			}
		}
	}

	/**
	 * searches for all existing files, which are imported in the given file and
	 * their imports and so on
	 * 
	 * @param file
	 *            where the imports are searched
	 * @param folder
	 *            parent folder of file
	 * @param validationResult
	 *            to add all imported files to the checked file list
	 * @return List<File> including all imported files in file
	 */
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
					e);
		}

		return importedFileList;
	}

	/**
	 * checks, if there are namespace and id duplicates in these two checked
	 * files
	 * 
	 * @param file1
	 *            first file to check
	 * @param file2
	 *            second file to check
	 * @param document1
	 *            parsed file1
	 * @param document2
	 *            parsed file2
	 * @param validationResult
	 *            for adding violations to the current validation result
	 * @throws XPathExpressionException
	 *             if there are invalid xpath expressions
	 */
	private void checkNamespacesAndIdDuplicates(File file1, File file2,
			Document document1, Document document2,
			ValidationResult validationResult) throws XPathExpressionException {
		NodeList foundNodes1 = (NodeList) xPathExpression.evaluate(document1,
				XPathConstants.NODESET);
		NodeList foundNodes2 = (NodeList) xPathExpression.evaluate(document2,
				XPathConstants.NODESET);
		String constraint = "EXT.002";
		for (int k = 1; k < foundNodes1.getLength(); k++) {
			String importedFile1Id = foundNodes1.item(k).getNodeValue();
			for (int l = 1; l < foundNodes2.getLength(); l++) {
				String importedFile2Id = foundNodes2.item(l).getNodeValue();
				if (importedFile1Id.equals(importedFile2Id)) {
					String xpathLocation = createIdBpmnExpression(importedFile1Id);
					validationResult.getViolations().add(
							new Violation(constraint, file1.getName(),
									xmlLocator.findLine(file1, xpathLocation),
									xpathLocation + "[0]",
									"Files have id duplicates"));
					validationResult.getViolations().add(
							new Violation(constraint, file2.getName(),
									xmlLocator.findLine(file2, xpathLocation),
									xpathLocation + "[0]",
									"Files have id duplicates"));
					logger.info("violation of constraint {} found.", constraint);
				}
			}
		}
	}

	/**
	 * creates a xpath expression for finding the id
	 * 
	 * @param id
	 *            the id, to which the expression should refer
	 * @return the xpath expression, which refers the given id
	 */
	private String createIdBpmnExpression(String id) {
		return "//bpmn:*[@id = '" + id + "']";
	}

}
