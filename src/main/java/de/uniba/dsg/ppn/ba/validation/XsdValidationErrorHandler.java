package de.uniba.dsg.ppn.ba.validation;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * 
 * @author Andreas Vorndran, Philipp Neugebauer
 * @version 1.0
 * 
 */
public class XsdValidationErrorHandler implements ErrorHandler {

	private List<SAXParseException> xsdErrorList = new ArrayList<>();

	public XsdValidationErrorHandler(List<SAXParseException> xsdErrorList) {
		this.xsdErrorList = xsdErrorList;
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		xsdErrorList.add(e);
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		xsdErrorList.add(e);
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		xsdErrorList.add(e);
	}

	public List<SAXParseException> getXsdErrors() {
		return xsdErrorList;
	}
}
