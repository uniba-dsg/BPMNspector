package de.uniba.dsg.ppn.ba.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class XmlWriter {

	public void writeXml(File file, List<String> fileText) {

		try (PrintWriter outputWriter = new PrintWriter(new BufferedWriter(
				new FileWriter(file)))) {
			String text = "";
			for (int i = 0; i < fileText.size(); i++) {
				text += fileText.get(i) + "\r\n";
			}
			outputWriter.println(text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}