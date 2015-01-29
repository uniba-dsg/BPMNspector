package de.uniba.dsg.bpmnspector.common.xsdvalidation;

import api.ValidationResult;
import api.ValidationException;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Super class for all validators to avoid code redundance
 *
 * @author Philipp Neugebauer
 * @author Matthias Geiger
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
     * @throws ValidationException
     */
    public abstract void validateAgainstXsd(File xmlFile,
            ValidationResult validationResult) throws IOException, SAXException,
            ValidationException;

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
