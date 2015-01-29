package api;

import java.util.Objects;

public final class Warning implements Comparable<Warning> {

    private final String message;
    private final Location location;

    public Warning(String message, Location location) {
        this.message = Objects.requireNonNull(message);
        this.location = Objects.requireNonNull(location);
    }

    public String getMessage() {
        return message;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Warning{" +
                "message='" + message + '\'' +
                ", indicator=" + location +
                '}';
    }

    @Override
    public int compareTo(Warning o) {
        Objects.requireNonNull(o);

        if (getMessage().equals(o.getMessage())) {
            return getLocation().compareTo(o.getLocation());
        } else {
            return getMessage().compareTo(o.getMessage());
        }
    }
}
