package microarch.delivery.core.domain.model;

import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import libs.ddd.ValueObject;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.Error;

public class Volume extends ValueObject<Volume> {

    private final int volume;

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

    public boolean greaterThan(Volume other) {
        if (other == null) {
            throw new NullPointerException("other");
        }

        return this.volume > other.volume;
    }

    public Result<Volume, Error> add(Volume other) {
        if (other == null) {
            return Result.failure(GeneralErrors.valueIsRequired("other"));
        }

        long sum = (long) this.volume + other.volume;

        if (sum > Integer.MAX_VALUE) {
            return Result.failure(Error.of("volume_overflow", "Суммарный объем превышает допустимое значение"));
        }

        return Volume.create((int) sum);
    }

    @Converter
    public static class VolumeConverter implements AttributeConverter<Volume, Integer> {

        @Override
        public Integer convertToDatabaseColumn(Volume volume) {
            return volume == null ? null : volume.getVolume();
        }

        @Override
        public Volume convertToEntityAttribute(Integer value) {
            return value == null ? null : Volume.create(value).getValueOrThrow();
        }
    }

}
