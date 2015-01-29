package api;

public final class LocationCoordinate {

    public static final LocationCoordinate EMPTY = new LocationCoordinate(-1, -1);

    private final int row;
    private final int column;

    public LocationCoordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String getId() {
        return String.format("%d,%d", row, column);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationCoordinate location = (LocationCoordinate) o;

        if (column != location.column) return false;
        if (row != location.row) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = row;
        result = 31 * result + column;
        return result;
    }

    @Override
    public String toString() {
        return "Location{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}
