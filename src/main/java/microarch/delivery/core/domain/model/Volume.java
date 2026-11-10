package microarch.delivery.core.domain.model;

import java.util.List;

import libs.ddd.ValueObject;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.Error;

public class Volume extends ValueObject<Location> {

    private int volume;

    private Volume(int volume) {
        this.volume = volume;
    }

    public int getVolume() {
        return this.volume;
    }

    public static Result<Volume, Error> create(int value) {
        if (value <= 0)
            return Result.failure(GeneralErrors.valueIsInvalid("volume", value));

        var volume = new Volume(value);
        return Result.success(volume);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(volume);
    }

}
