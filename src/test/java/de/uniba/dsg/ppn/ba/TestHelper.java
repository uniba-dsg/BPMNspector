package de.uniba.dsg.ppn.ba;

import java.io.File;
import java.nio.file.Paths;

public class TestHelper {

    public static String getTestFilePath() {
        return Paths.get(System.getProperty("user.dir"))
                .resolve("src/test/resources").toString()
                + File.separator;
    }
}
