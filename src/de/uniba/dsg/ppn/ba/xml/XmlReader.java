package de.uniba.dsg.ppn.ba.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class XmlReader {

	public ArrayList<String> readXmlFile(File file) {

		ArrayList<String> lineList = new ArrayList<String>();

		try (BufferedReader inputReader = new BufferedReader(new FileReader(
				file))) {
			String line = null;
			while ((line = inputReader.readLine()) != null) {
				if (line.contains("bpmndi:")) {
					break;
				}
				if (!line.contains("definitions")) {
					if (!line.contains("?xml")) {
						line = line.substring(2);
					}
					lineList.add(line);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lineList;
	}
}