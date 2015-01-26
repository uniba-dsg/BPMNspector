package de.uniba.dsg.bpmnspector.common.xsdvalidation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.transform.stream.StreamSource;

import de.uniba.dsg.bpmnspector.refcheck.ValidatorException;
import org.xml.sax.SAXException;

import de.uniba.dsg.bpmnspector.common.ValidationResult;

/**
 * Super class for all validators to avoid code redundance
 *
 * @author Philipp Neugebauer
 * @version 1.0
 *
 */
public abstract class AbstractXsdValidator {

    /**
     * Validates the given xmlFile with the xsd files and writes violations to
     * the given validation result
     *
     * @param xmlFile
     *            the xml file which should be validated
     * @param validationResult
     *            the result object of the validation
     * @throws IOException
     *             when xmlFile can't be read
     * @throws SAXException
     *             when validation process fails somehow
     */
    public abstract void validateAgainstXsd(File xmlFile,
            ValidationResult validationResult) throws IOException, SAXException,
            ValidatorException;

    /**
     *
     * The method simplifies the search for a resource and returns the
     * streamsource with the searched source
     *
     * @param resourceName
     *            the name of the resource file
     * @return the streamsource of the file to be load
     * @throws FileNotFoundException
     *             if the resource file doesn't exist there
     */
    protected StreamSource resolveResourcePaths(String resourceName)
            throws FileNotFoundException {
        return new StreamSource(this.getClass().getResourceAsStream(
                "/" + resourceName));
    }
}
