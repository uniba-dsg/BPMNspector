package de.uniba.dsg.ppn.ba.helper;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import de.uniba.dsg.bpmn.ValidationResult;

public class XmlWriter {

	private JAXBContext context;
	private Marshaller marshaller;
	private Logger logger;

	public XmlWriter() {
		logger = (Logger) LoggerFactory.getLogger("BpmnValidator");
		try {
			context = JAXBContext.newInstance(ValidationResult.class);
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		} catch (JAXBException e) {
			logger.error("jaxb writer initialization failed: {}", e.getCause());
		}
	}

	public void writeResult(ValidationResult result, File file)
			throws JAXBException {
		marshaller.marshal(result, file);
	}

}