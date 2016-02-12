package de.uniba.dsg.bpmnspector.common.util;

import api.Resource;
import api.ValidationException;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceUtils {

    public static Resource determineAndCreateResourceFromString(String location, String baseLocation)
            throws ValidationException {
        try {
            Resource resource;
            URI importUri = new URI(location);

            if(importUri.isAbsolute() && importUri.getScheme().toLowerCase().startsWith("http")) {
                // process as URL
                URL asURL = importUri.toURL();
                resource = new Resource(asURL);
            } else {
                // process as file
                Path importPath = Paths.get(importUri);
                if (!importPath.isAbsolute() && baseLocation != null) {
                    // resolve relative path based on the baseLocation
                    importPath = Paths.get(baseLocation).getParent().resolve(importPath).normalize().toAbsolutePath();
                }

                if (Files.notExists(importPath) || !Files.isRegularFile(importPath)) {
                    throw new ValidationException("File does not exist.");
                } else {
                    resource = new Resource(importPath);
                }
            }
            return resource;
        } catch (URISyntaxException | MalformedURLException | InvalidPathException e ) {
            throw new ValidationException("Path " + location + " is invalid.", e);
        }
    }
}
