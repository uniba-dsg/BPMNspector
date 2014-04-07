package de.uniba.dsg.ppn.ba.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class XmlReader {

	public String readXmlFile(File file) throws FileNotFoundException,
			IOException {

		StringBuffer text = new StringBuffer();

		try (BufferedReader inputReader = new BufferedReader(new FileReader(
				file))) {
			String line = null;
			while ((line = inputReader.readLine()) != null) {
				if (!line.contains("bpmndi:") && !line.contains("dc:Bounds")
						&& !line.contains("di:waypoint")) {
					text.append(line + "\r\n");
				}
			}
		}

		return text.toString();
	}

	public String readImportedXmlFile(File file) throws FileNotFoundException,
			IOException {
		StringBuffer text = new StringBuffer();

		try (BufferedReader inputReader = new BufferedReader(new FileReader(
				file))) {
			String line = null;
			while ((line = inputReader.readLine()) != null) {
				if (!line.contains("bpmndi:") && !line.contains("definitions")
						&& !line.contains("?xml version")
						&& !line.contains("dc:Bounds")
						&& !line.contains("di:waypoint")) {
					text.append(line + "\r\n");
				}
			}
		}

		return text.toString();
	}

}