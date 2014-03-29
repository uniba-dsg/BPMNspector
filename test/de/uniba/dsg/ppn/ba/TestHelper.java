package de.uniba.dsg.ppn.ba;

public class TestHelper {

	public static String getTestFilePath() {
		String path = TestHelper.class.getResource("").toString();
		return path.substring(5, path.indexOf("schematron") - 1);
	}
}
