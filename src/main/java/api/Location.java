package api;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public final class Location implements Comparable<Location> {

	private final Optional<LocationCoordinate> location;
	private final Optional<String> xpath;

	private final Path fileName;

	public Location(Path fileName, LocationCoordinate location) {
		this.fileName = Objects.requireNonNull(fileName);
		this.location = Optional.of(location);
		this.xpath = Optional.empty();
	}

	public Location(Path fileName, LocationCoordinate location, String xpath) {
		this.fileName = Objects.requireNonNull(fileName);
		this.location = Optional.of(location);
		this.xpath = Optional.of(xpath);
	}

	public LocationCoordinate getLocation() {
		return location.orElse(LocationCoordinate.EMPTY);
	}

	public Optional<String> getXpath() {
		return xpath;
	}

	public Path getFileName() {
		return fileName;
	}

	@Override
	public int compareTo(Location o) {
		Objects.requireNonNull(o);

		if (isSameFile(o)) {
			return getLocation().getId().compareTo(o.getLocation().getId());
		} else {
			return fileName.compareTo(o.fileName);
		}
	}

	private boolean isSameFile(Location o) {
		return fileName.equals(o.fileName);
	}

}
