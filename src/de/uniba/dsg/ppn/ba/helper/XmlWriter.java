package de.uniba.dsg.ppn.ba.helper;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.uniba.dsg.bpmn.ValidationResult;

public class XmlWriter {

	public void writeResult(ValidationResult result, File file)
			throws JAXBException {

		JAXBContext context = JAXBContext.newInstance(ValidationResult.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		marshaller.marshal(result, file);
	}

}