package de.uniba.dsg.bpmnspector.refcheck.importer;

import java.util.List;
import java.util.Map;

import org.jdom2.Document;

/**
 * Class to represent a BPMN file and all referenced files.
 * 
 * Currently referenced BPMN, WSDL and XSD files are supported.
 * 
 * @author Matthias Geiger
 * @version 1.0
 */
public class ProcessFileSet {

	public static final String BPMN2_NAMESPACE = "http://www.omg.org/spec/BPMN/20100524/MODEL";
	public static final String WSDL_NAMESPACE = "http://www.w3.org/TR/wsdl20/";
	public static final String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema";

	private final Document bpmnBaseFile;
	private List<Document> referencedBpmnFiles;
	private List<Document> referencedWsdlFiles;
	private List<Document> referencedXSDFiles;
	private final List<String> processedFiles;

	/**
	 * Creates a file set containing a base BPMN file representation as a
	 * {@link org.jdom2.Document}, the Map should use the defined constants as
	 * Keys.
	 * 
	 * @param baseFile - Document representation of the base BPMN file
	 * @param map
	 *            - map containing Lists of all referenced BPMN, WSDL and XSD
	 *            documents
	 */
	public ProcessFileSet(Document baseFile, Map<String, List<Document>> map,
			List<String> processedFiles) {
		this.bpmnBaseFile = baseFile;
		if (map != null) {
			referencedBpmnFiles = map.get(BPMN2_NAMESPACE);
			referencedWsdlFiles = map.get(WSDL_NAMESPACE);
			referencedXSDFiles = map.get(XSD_NAMESPACE);
		}
		this.processedFiles = processedFiles;
	}

	/**
	 * Getter for the base BPMN file representation
	 * 
	 * @return - returns the base BPMN process
	 */
	public Document getBpmnBaseFile() {
		return bpmnBaseFile;
	}

	/**
	 * Getter for all referenced BPMN files
	 * 
	 * @return - returns the List of referenced BPMN files
	 */
	public List<Document> getReferencedBpmnFiles() {
		return referencedBpmnFiles;
	}

	/**
	 * Getter for all referenced WSDL files
	 * 
	 * @return - returns the List of referenced WSDL files
	 */
	public List<Document> getReferencedWsdlFiles() {
		return referencedWsdlFiles;
	}

	/**
	 * Getter for all referenced XSD files
	 * 
	 * @return - returns the List of referenced XSD files
	 */
	public List<Document> getReferencedXSDFiles() {
		return referencedXSDFiles;
	}
	
	/**
	 * Getter for the list of all processed file paths
	 * 
	 * @return - returns the list of all processed file paths
	 */
	public List<String> getProcessedFiles() {
		return processedFiles;
	}

}
