package api;

import java.util.Objects;

public final class Violation implements Comparable<Violation> {

	// what
	private final String constraint;
	private final String message;

	// where
	private final Location location;

	public Violation(Location location, String message, String constraint) {
		this.location = Objects.requireNonNull(location);
		this.message = Objects.requireNonNull(message);
		this.constraint = Objects.requireNonNull(constraint);
	}

	public String getConstraint() {
		return constraint;
	}

	public String getMessage() {
		return message;
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return "Violation{" +
				"constraint='" + constraint + '\'' +
				", message='" + message + '\'' +
				", indicator=" + location +
				'}';
	}

	@Override
	public int compareTo(Violation o) {
		Objects.requireNonNull(o);

		if(getConstraint().equals(o.getConstraint())){
			if(getMessage().equals(o.getMessage())) {
				return getLocation().compareTo(o.getLocation());
			} else {
				return getMessage().compareTo(o.getMessage());
			}
		} else {
			return getConstraint().compareTo(o.getConstraint());
		}
	}

}
