package de.uniba.dsg.ppn.ba.validation;

import java.io.FileNotFoundException;

import javax.xml.transform.stream.StreamSource;

public class MyValidator {

	/**
	 * 
	 * The method simplifies the search for a resource and returns the
	 * streamsource with the searched source
	 * 
	 * @param resourceName
	 * @return the streamsource of the file, which needed to be load
	 * @throws FileNotFoundException
	 *             if the resource file doesn't exist there
	 */
	protected StreamSource resolveResourcePaths(String resourceName)
			throws FileNotFoundException {
		return new StreamSource(this.getClass().getResourceAsStream(
				"/" + resourceName));
	}
}
