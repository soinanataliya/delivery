package microarch.delivery.core.domain.model.courier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import libs.ddd.BaseEntity;
import libs.errs.Result;
import libs.errs.UnitResult;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import lombok.Getter;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Status;

import java.util.UUID;

@Entity
@Table(name = "assignments")

public final class Assignment extends BaseEntity<UUID> {

    @Column(name = "orderId")
    @Getter
    private final UUID orderId;

    @Column(name = "volume")
    @Getter
    private final Volume volume;

    @Column(name = "location")
    @Getter
    private final Location location;

    @Column(name = "status")
    @Getter
    private Status status;

    private Assignment(UUID orderId, Volume volume, Location location, Status status) {
        super(UUID.randomUUID());
        this.orderId = orderId;
        this.volume = volume;
        this.location = location;
        this.status = status;
    }

    public static Result<Assignment, Error> create(UUID orderId, Volume volume, Location location) {

        if (orderId == null)
            return Result.failure(GeneralErrors.valueIsRequired("orderId"));
        if (volume == null)
            return Result.failure(GeneralErrors.valueIsRequired("volume"));
        if (location == null)
            return Result.failure(GeneralErrors.valueIsRequired("location"));

        var assignment = new Assignment(orderId, volume, location, Status.Assigned);
        return Result.success(assignment);
    }

    public UnitResult<Error> complete(Location courierLocation) {
        if (courierLocation == null) {
            return UnitResult.failure(GeneralErrors.valueIsRequired("courierLocation"));
        }

        if (status == Status.Completed) {
            return UnitResult.failure(Error.of("already_completed", "Назначение уже завершено"));
        }

        if (location.distanceTo(courierLocation) > 1) {
            return UnitResult
                    .failure(Error.of("courier_too_far", "Курьер должен находиться не дальше одной клетки от заказа"));
        }
        this.status = Status.Completed;
        return UnitResult.success();

    }
}
