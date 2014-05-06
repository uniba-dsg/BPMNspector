package de.uniba.dsg.ppn.ba.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import de.uniba.dsg.bpmn.ValidationResult;

/**
 * This class is used for writing xml files via jaxb
 * 
 * @author Philipp Neugebauer
 * @version 1.0
 * 
 */
public class XmlWriter {

	private JAXBContext context;
	private Marshaller marshaller;
	private Logger logger;

	public XmlWriter() {
		logger = (Logger) LoggerFactory.getLogger(getClass().getSimpleName());
		try {
			context = JAXBContext.newInstance(ValidationResult.class);
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (JAXBException e) {
			logger.error("jaxb writer initialization failed: {}", e);
		}
	}

	/**
	 * writes the result to the given file
	 * 
	 * @param result
	 *            the validation result, which should be writen to a file
	 * @param file
	 *            the file, where the validation result should be written to
	 * @throws JAXBException
	 *             if an error occurs during xml writing process
	 */
	public void writeResult(ValidationResult result, File file)
			throws JAXBException {
		marshaller.marshal(result, file);
	}

}