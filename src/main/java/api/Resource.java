package api;

import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class Resource implements Comparable<Resource> {

    private final String resourceName;

    private final URL url;

    private final Path path;

    private final ResourceType type;

    public Resource(Path path) {
        Objects.requireNonNull(path);

        this.path = path;
        resourceName = path.toAbsolutePath().toString();
        type = ResourceType.FILE;

        url = null;
    }

    public Resource(URL url) {
        Objects.requireNonNull(url);

        this.url = url;
        this.resourceName = url.toString();
        this.type = ResourceType.URL;

        path = null;
    }

    public Resource(String resourceName) {
        this.resourceName = resourceName;
        this.type = ResourceType.STREAM;

        this.path = null;
        this.url = null;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Optional<URL> getUrl() {
        return Optional.ofNullable(url);
    }

    public Optional<Path> getPath() {
        return Optional.ofNullable(path);
    }

    public ResourceType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        if (type != resource.type) return false;

        return resourceName.equals(resource.resourceName);
    }

    @Override
    public int hashCode() {
        return resourceName.hashCode();
    }

    @Override
    public int compareTo(Resource resource) {
        Objects.requireNonNull(resource);

        return resourceName.compareTo(resource.getResourceName());
    }

    @Override
    public String toString() {
        return resourceName;
    }

    public enum ResourceType {
        URL, FILE, STREAM
    }
}
