package de.uniba.dsg.bpmnspector.common.xsdvalidation;

import java.io.InputStream;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Class for representing a custom LSResourceResolver in order to enable
 * resource resolution for BPMN XSD Validation.
 *
 * Needed for &lt;xs:include&gt;-resolution when generating a
 * {@link javax.xml.validation.Schema} when using
 * getClass().getResourceAsStream("path/to/file.xsd")
 *
 * @author Matthias Geiger
 * @version 1.0
 * @see LSResourceResolver
 */
public class ResourceResolver implements LSResourceResolver {

    /*
     * (non-Javadoc)
     * 
     * @see org.w3c.dom.ls.LSResourceResolver#resolveResource(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public LSInput resolveResource(String type, String namespaceURI,
            String publicId, String systemId, String baseURI) {

        InputStream resourceAsStream = this.getClass().getResourceAsStream(
                "/" + systemId);

        return new Input(publicId, systemId, resourceAsStream);
    }
}
