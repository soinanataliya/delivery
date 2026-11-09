package microarch.delivery.core.domain.model;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import libs.ddd.ValueObject;
import libs.errs.Result;
import libs.errs.Error;
import libs.errs.GeneralErrors;

public class Location extends ValueObject<Location> {

    private static final int MIN_COORDINATE = 1;
    private static final int MAX_COORDINATE = 10;

    private final int x;
    private final int y;

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
        if (x < MIN_COORDINATE || x > MAX_COORDINATE)
            return Result.failure(GeneralErrors.valueIsOutOfRange("x", x, MIN_COORDINATE, MAX_COORDINATE));
        if (y < MIN_COORDINATE || y > MAX_COORDINATE)
            return Result.failure(GeneralErrors.valueIsOutOfRange("y", y, MIN_COORDINATE, MAX_COORDINATE));

        var location = new Location(x, y);
        return Result.success(location);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(x, y);
    }

    public int distanceTo(@NotNull Location other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

}
