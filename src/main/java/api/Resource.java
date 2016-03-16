package api;

import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class Resource implements Comparable<Resource> {

    private final String resourceName;

    private final Optional<URL> url;

    private final Optional<Path> path;

    private final ResourceType type;

    public Resource(Path path) {
        this.path = Optional.of(path);
        resourceName = path.toAbsolutePath().toString();
        type = ResourceType.FILE;

        url = Optional.empty();
    }

    public Resource(URL url) {
        this.url = Optional.of(url);
        this.resourceName = url.toString();
        this.type = ResourceType.URL;

        path = Optional.empty();
    }

    public Resource(String resourceName) {
        this.resourceName = resourceName;
        this.type = ResourceType.STREAM;

        this.path = Optional.empty();
        this.url = Optional.empty();
    }

    public String getResourceName() {
        return resourceName;
    }

    public Optional<URL> getUrl() {
        return url;
    }

    public Optional<Path> getPath() {
        return path;
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
