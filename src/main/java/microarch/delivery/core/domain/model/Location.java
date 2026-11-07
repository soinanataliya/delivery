package microarch.delivery.core.domain.model;

import java.util.List;

import libs.ddd.ValueObject;
import libs.errs.Result;
import libs.errs.Error;
import libs.errs.GeneralErrors;

public class Location extends ValueObject<Location> {
    private int x;
    private int y;

    private Location() {
    }

    private Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public static Result<Location, Error> create(int x, int y) {
        if (x <= 0 || x > 10)
            return Result.failure(GeneralErrors.valueIsOutOfRange("x", x, 1, 10));
        if (y <= 0 || y > 10)
            return Result.failure(GeneralErrors.valueIsOutOfRange("y", y, 1, 10));

        var location = new Location(x, y);
        return Result.success(location);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(x, y);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Location location)) {
            return false;
        }

        return x == location.x && y == location.y;
    }

    public int distanceTo(Location other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

}
