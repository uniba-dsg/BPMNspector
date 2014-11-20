package de.uniba.dsg.bpmnspector.refcheck.importer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.jdom2.util.IteratorIterable;
import org.xml.sax.SAXException;

import de.uniba.dsg.bpmnspector.common.ValidationResult;
import de.uniba.dsg.bpmnspector.common.xsdvalidation.BpmnXsdValidator;
import de.uniba.dsg.bpmnspector.refcheck.ValidatorException;

/**
 * Helper Class to Import all referenced BPMN, WSDL and XSD files referenced by
 * a BPMN process.
 * 
 * @author Matthias Geiger, Andreas Vorndran
 * @version 1.0
 * 
 * @see ValidatorException
 * 
 */
public class FileImporter {

	private Properties language;
	private final SAXBuilder builder;
	private final BpmnXsdValidator bpmnXsdValidator;

	/**
	 * Constructs a new {@code FileImporter}
	 * 
	 * @param language
	 *            - in order to enable language specific error messages language
	 *            dependent properties are needed
	 */
	public FileImporter(Properties language) {
		this.language = language;
		builder = new SAXBuilder();
		builder.setJDOMFactory(new LocatedJDOMFactory());
		bpmnXsdValidator = new BpmnXsdValidator();
	}

	/**
	 * Setter to change Language after initial creation.
	 * 
	 * @param language
	 *            - the new language properties to use
	 */
	public void setLanguage(Properties language) {
		this.language = language;
	}

	/**
	 * Load all the file found at {@code pathToBaseFile}
	 * 
	 * @param pathToBaseFile
	 *            - the base file for import processing, this file is always
	 *            imported
	 * @param processImports
	 *            - if {@code true} all files referenced by the base file will
	 *            also be imported
	 * @return - A {@link ProcessFileSet} containing the
	 *         {@link org.jdom2.Document} representation of all imported files
	 * @throws ValidatorException
	 *             - thrown if either a file cannot be resolved or the file is
	 *             not schema valid
	 */
	public ProcessFileSet loadAllFiles(String pathToBaseFile,
			boolean processImports) throws ValidatorException {

		File baseFile = checkPathAndCreateFile(pathToBaseFile);

		try {
			bpmnXsdValidator.validateAgainstXsd(baseFile, new ValidationResult());
			Document baseDoc = builder.build(baseFile);
			List<String> processedFiles = new ArrayList<>();
			processedFiles.add(baseFile.getAbsolutePath());
			if (processImports) {
				return new ProcessFileSet(baseDoc, processImports(baseDoc,
						baseFile.toPath(), processedFiles), processedFiles);
			} else {
				return new ProcessFileSet(baseDoc, null, null);
			}

		} catch (SAXException e) {
			String errorText = language.getProperty("validator.xsd.sax")+": "+e.getMessage();
			throw new ValidatorException(errorText, e);
		} catch (JDOMException e) {
			String errorText = language.getProperty("validator.jdom") + "'"
					+ pathToBaseFile + "'.";
			throw new ValidatorException(errorText, e);
		} catch (IOException e) {

			String errorText = language.getProperty("validator.io") + "'"
					+ pathToBaseFile + "'.";
			throw new ValidatorException(errorText, e);
		} 

	}

	private File checkPathAndCreateFile(String path) throws ValidatorException {
		if (path == null || path.equals("")) {
			throw new ValidatorException(
					language.getProperty("importer.path.notempty.notnull"),
					new IllegalArgumentException(language
							.getProperty("importer.path.notempty.notnull")));
		}
		Path file = Paths.get(path);
		if (Files.notExists(file) || Files.isDirectory(file)) {
			throw new ValidatorException(
					language.getProperty("importer.path.invalid")
							+ path
							+ language
									.getProperty("importer.path.notexistentOrDir"));
		}

		return file.toFile();
	}

	private Map<String, List<Document>> processImports(Document baseDoc,
			Path baseDocPath, List<String> processedFiles)
			throws ValidatorException {
		Map<String, List<Document>> resolvedFiles = new HashMap<>();
		Element rootNode = baseDoc.getRootElement();
		Filter<Element> filter = Filters.element();
		IteratorIterable<Element> list = rootNode.getDescendants(filter);

		List<Document> resolvedBpmnFiles = new ArrayList<>();
		List<Document> resolvedXsdFiles = new ArrayList<>();
		List<Document> resolvedWsdlFiles = new ArrayList<>();

		while (list.hasNext()) {
			Element element = list.next();
			if ("import".equals(element.getName())
					&& "http://www.omg.org/spec/BPMN/20100524/MODEL"
							.equals(element.getNamespaceURI())) {
				String path = element.getAttributeValue("location");

				// Navigate from the folder containing the baseFile
				// (baseFile.getParent()) to the given location and
				// normalize the result
				String absPath = baseDocPath.getParent()
						.resolve(Paths.get(path)).normalize().toString();
				File file = checkPathAndCreateFile(absPath);
				if (!processedFiles.contains(file.getAbsolutePath())) {
					if (ProcessFileSet.BPMN2_NAMESPACE.equals(element
							.getAttributeValue("importType"))) {

						try {
							
							bpmnXsdValidator.validateAgainstXsd(file, new ValidationResult());
							Document bpmnDoc = builder.build(file);
							processedFiles.add(file.getAbsolutePath());
							resolvedBpmnFiles.add(bpmnDoc);
							Map<String, List<Document>> processResults = processImports(
									bpmnDoc, file.toPath(), processedFiles);
							resolvedBpmnFiles.addAll(processResults
									.get(ProcessFileSet.BPMN2_NAMESPACE));
							resolvedWsdlFiles.addAll(processResults
									.get(ProcessFileSet.WSDL_NAMESPACE));
							resolvedXsdFiles.addAll(processResults
									.get(ProcessFileSet.XSD_NAMESPACE));

						} catch (SAXException e) {
							String errorText = language.getProperty("validator.xsd.sax")+": "+e.getMessage();
							throw new ValidatorException(errorText, e);
						}catch (JDOMException e) {
							String errorText = language
									.getProperty("validator.jdom.imported.part1")
									+ path
									+ "("
									+ absPath
									+ ")"
									+ language
											.getProperty("validator.jdom.imported.part2");
							throw new ValidatorException(errorText, e);
						} catch (IOException e) {
							String errorText = language
									.getProperty("validator.io.imorted.part1")
									+ path
									+ "("
									+ absPath
									+ ")"
									+ language
											.getProperty("validator.io.imorted.part2");
							throw new ValidatorException(errorText, e);
						} 
					} else if (ProcessFileSet.WSDL_NAMESPACE.equals(element
							.getAttributeValue("importType"))) {
						// TODO implement import processing of WSDL interfaces
					} else if (ProcessFileSet.XSD_NAMESPACE.equals(element
							.getAttributeValue("importType"))) {
						// TODO implement import processing of XSDs
					}
				}
			}
		}
		resolvedFiles.put(ProcessFileSet.BPMN2_NAMESPACE, resolvedBpmnFiles);
		resolvedFiles.put(ProcessFileSet.WSDL_NAMESPACE, resolvedWsdlFiles);
		resolvedFiles.put(ProcessFileSet.XSD_NAMESPACE, resolvedXsdFiles);
		return resolvedFiles;
	}

}
