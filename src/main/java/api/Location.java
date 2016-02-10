package api;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public final class Location implements Comparable<Location> {

	private final Optional<LocationCoordinate> location;
	private final Optional<String> xpath;

	private final String resourceName;
	private final Optional<Path> filePath;

	public Location(Path filePath, LocationCoordinate location) {
		this.filePath = Optional.of(filePath);
		this.location = Optional.of(location);
		this.xpath = Optional.empty();
		this.resourceName = filePath.toAbsolutePath().toString();
	}

	public Location(Path filePath, LocationCoordinate location, String xpath) {
		this.filePath = Optional.of(filePath);
		this.location = Optional.of(location);
		this.xpath = Optional.of(xpath);
		this.resourceName = filePath.getFileName().toString();
	}

	public Location(String resourceName, LocationCoordinate location) {
		this.resourceName = Objects.requireNonNull(resourceName);
		this.location = Optional.of(location);
		this.xpath = Optional.empty();
		this.filePath = Optional.empty();
	}

	public Location(String resourceName, LocationCoordinate location, String xpath) {
		this.resourceName = Objects.requireNonNull(resourceName);
		this.location = Optional.of(location);
		this.xpath = Optional.of(xpath);
		this.filePath = Optional.empty();
	}



	public LocationCoordinate getLocation() {
		return location.orElse(LocationCoordinate.EMPTY);
	}

	public Optional<String> getXpath() {
		return xpath;
	}

	public Optional<Path> getFilePath() {
		return filePath;
	}

	public String getResourceName() {
		return resourceName;
	}

	@Override
	public int compareTo(Location o) {
		Objects.requireNonNull(o);

		if (isSameFile(o)) {
			return getLocation().getId().compareTo(o.getLocation().getId());
		} else {
			if(filePath.isPresent() && o.filePath.isPresent()) {
				return filePath.get().compareTo(o.filePath.get());
			} else {
				return resourceName.compareTo(o.resourceName);
			}
		}
	}

	private boolean isSameFile(Location o) {
		return filePath.equals(o.filePath);
	}

}
