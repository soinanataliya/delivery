package microarch.delivery.core.domain.model.courier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import libs.ddd.Aggregate;
import libs.errs.Result;
import libs.errs.UnitResult;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import lombok.Getter;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "couriers")

public final class Courier extends Aggregate<UUID> {

    @Column(name = "name")
    @Getter
    private final String name;

    @Column(name = "maxVolume")
    @Getter
    private final Volume maxVolume;

    @Column(name = "location")
    @Getter
    private Location location;

    @Column(name = "assignments")
    private final List<Assignment> assignments;

    private Courier(String name, Location location) {
        super(UUID.randomUUID());
        this.name = name;
        this.maxVolume = Volume.create(20).getValueOrThrow();
        this.location = location;
        this.assignments = new ArrayList<>();
    }

    public static Result<Courier, Error> create(String name, Location location) {

        if (name == null || name.isBlank())
            return Result.failure(GeneralErrors.valueIsRequired("name"));
        if (location == null)
            return Result.failure(GeneralErrors.valueIsRequired("location"));

        var courier = new Courier(name, location);
        return Result.success(courier);
    }

    public List<Assignment> getAssignments() {
        return List.copyOf(assignments);
    }

    public UnitResult<Error> canTakeOrder(Volume newOrderVolume) {
        if (newOrderVolume == null) {
            return UnitResult.failure(GeneralErrors.valueIsRequired("newOrderVolume"));
        }

        Volume totalVolume = newOrderVolume;

        for (Assignment assignment : assignments) {
            if (assignment.getStatus() != Status.Completed) {
                var sumResult = totalVolume.add(assignment.getVolume());

                if (sumResult.isFailure()) {
                    return UnitResult.failure(sumResult.getError());
                }

                totalVolume = sumResult.getValue();
            }
        }

        if (totalVolume.greaterThan(maxVolume)) {
            return UnitResult.failure(Error.of("volume_exceeded", "Объем заказов превышает вместимость курьера"));
        }

        return UnitResult.success();
    }

    public UnitResult<Error> assignOrder(Order order) {
        if (order == null) {
            return UnitResult.failure(GeneralErrors.valueIsRequired("order"));
        }

        for (Assignment assignment : assignments) {
            if (assignment.getOrderId().equals(order.getId())) {
                return UnitResult.failure(Error.of("order_already_taken", "Курьер уже принимал этот заказ"));
            }
        }

        var volumeResult = canTakeOrder(order.getVolume());

        if (volumeResult.isFailure()) {
            return UnitResult.failure(volumeResult.getError());
        }

        var assignmentResult = Assignment.create(order.getId(), order.getVolume(), order.getLocation());

        if (assignmentResult.isFailure()) {
            return UnitResult.failure(assignmentResult.getError());
        }
        this.assignments.add(assignmentResult.getValue());
        return UnitResult.success();
    }

    public UnitResult<Error> completeAssignment(UUID assignmentId) {
        if (assignmentId == null) {
            return UnitResult.failure(GeneralErrors.valueIsRequired("assignmentId"));
        }

        for (Assignment assignment : assignments) {
            if (assignment.getId().equals(assignmentId)) {
                return assignment.complete(this.location);
            }
        }

        return UnitResult.failure(Error.of("assignment_not_found", "Назначение не найдено у курьера"));
    }

    public UnitResult<Error> moveTo(Location destination) {
        if (destination == null) {
            return UnitResult.failure(GeneralErrors.valueIsRequired("destination"));
        }

        if (this.location.distanceTo(destination) > 1) {
            return UnitResult.failure(
                    Error.of("destination_too_far", "Курьер может переместиться не больше чем на одну клетку"));
        }

        this.location = destination;

        return UnitResult.success();
    }
}
