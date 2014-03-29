package de.uniba.dsg.ppn.ba;

import java.io.File;

public class TestHelper {

	public static String getTestFilePath() {
		String path = TestHelper.class.getResource("").toString();
		return path.substring(5, path.indexOf("schematron") - 1)
				+ File.separator + "testprocesses" + File.separator;
	}
}
