package de.uniba.dsg.ppn.ba.validation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import de.uniba.dsg.bpmn.ValidationResult;

/**
 * Super class for all validators to avoid code redundance
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public class XsdValidator {

	/**
	 * Validates the given xmlFile with the xsd files and writes violations to
	 * the given validation result
	 *
	 * @param xmlFile
	 * @param validationResult
	 * @throws IOException
	 *             when xmlFile can't be read
	 * @throws SAXException
	 *             when validation process fails somehow
	 */
	public void validateAgainstXsd(File xmlFile,
			ValidationResult validationResult) throws IOException, SAXException {

	}

	/**
	 *
	 * The method simplifies the search for a resource and returns the
	 * streamsource with the searched source
	 *
	 * @param resourceName
	 * @return the streamsource of the file, which needed to be load
	 * @throws FileNotFoundException
	 *             if the resource file doesn't exist there
	 */
	protected StreamSource resolveResourcePaths(String resourceName)
			throws FileNotFoundException {
		return new StreamSource(this.getClass().getResourceAsStream(
				"/" + resourceName));
	}
}
