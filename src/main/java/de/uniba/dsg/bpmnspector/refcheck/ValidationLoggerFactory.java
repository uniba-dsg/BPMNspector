package de.uniba.dsg.bpmnspector.refcheck;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * This class creates logger for the validation process. A logger can be created
 * for a file or for the general purpose. It has a file output in the log folder
 * with files ending with '-log.txt' and a console output.
 * 
 * @author Andreas Vorndran
 * @version 1.0
 * 
 */
public class ValidationLoggerFactory {

	/**
	 * This static method creates a new logger for the given file.
	 * 
	 * @param fileName
	 *            the name of the file to build the logger for or null (for the
	 *            general part: uses log.txt)
	 * @param level
	 *            the log level (off, severe or info). Default (null) is off.
	 * @param language
	 *            the language properties (for the exception messages)
	 * @return the new logger
	 * @throws ValidatorException
	 *             if a technical problem exists while creating the logger
	 */
	public static Logger createLogger(String fileName, Level level,
			Properties language) throws ValidatorException {
		try {
			Logger logger;
			FileHandler fileTxt;
			if (fileName == null) {
				logger = Logger.getLogger("log");
			} else {
				logger = Logger.getLogger(fileName);
			}
			if (level == null) {
				logger.setLevel(Level.OFF);
			} else {
				logger.setLevel(level);
				// if logger is active create the file handler
				if (level != Level.OFF) {
					File dir = new File("log");
					if (!dir.exists()) {
						dir.mkdir();
					}
					if (fileName == null) {
						fileTxt = new FileHandler("log/log.txt");
					} else {
						fileTxt = new FileHandler("log/" + fileName
								+ "-log.txt");
					}
					SimpleFormatter formatterTxt = new SimpleFormatter();
					fileTxt.setFormatter(formatterTxt);
					logger.addHandler(fileTxt);
				}
			}
			logger.setUseParentHandlers(false);
			return logger;
		} catch (SecurityException e) {
			if (fileName == null) {
				throw new ValidatorException(
						language.getProperty("logger.security.part1")
								+ "log.txt' "
								+ language.getProperty("logger.security.part2"),
						e);
			} else {
				throw new ValidatorException(
						language.getProperty("logger.security.part1")
								+ fileName + "-log.txt' "
								+ language.getProperty("logger.security.part2"),
						e);
			}
		} catch (IOException e) {
			if (fileName == null) {
				throw new ValidatorException(
						language.getProperty("logger.io.part1") + "log.txt' "
								+ language.getProperty("logger.io.part2"), e);
			} else {
				throw new ValidatorException(
						language.getProperty("logger.io.part1") + fileName
								+ "-log.txt' "
								+ language.getProperty("logger.io.part2"), e);
			}
		}
	}

}
