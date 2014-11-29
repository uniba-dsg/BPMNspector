package de.uniba.dsg.ppn.ba.validation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class DocumentTransformer {

    /**
     * transforms the given headFileDocument to an inputstream, so that it can
     * be used for the schematron validation process
     *
     * @param headFileDocument
     * @return input stream with the head file document
     * @throws UnsupportedEncodingException
     *             if the encoding isn't supported
     * @throws TransformerException
     *             if anything fails during transformation process
     */
    public static ByteArrayInputStream transformToInputStream(
            Document headFileDocument) throws UnsupportedEncodingException,
            TransformerException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer
                .transform(new DOMSource(headFileDocument), new StreamResult(
                        new OutputStreamWriter(outputStream, "UTF-8")));

        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
