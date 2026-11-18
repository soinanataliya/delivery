package microarch.delivery.core.domain.model.order;

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
import microarch.delivery.core.domain.model.Location;
import java.util.UUID;

@Entity
@Table(name = "orders")

public final class Order extends Aggregate<UUID> {

    @Column(name = "volume")
    @Getter
    private final Volume volume;

    @Column(name = "location")
    @Getter
    private final Location location;

    @Column(name = "status")
    @Getter
    private OrderStatus status;

    private Order(UUID id, Volume volume, Location location, OrderStatus status) {
        super(id);
        this.volume = volume;
        this.location = location;
        this.status = status;
    }

    public static Result<Order, Error> create(UUID id, Volume volume, Location location) {

        if (id == null)
            return Result.failure(GeneralErrors.valueIsRequired("orderId"));
        if (volume == null)
            return Result.failure(GeneralErrors.valueIsRequired("volume"));
        if (location == null)
            return Result.failure(GeneralErrors.valueIsRequired("location"));

        var order = new Order(id, volume, location, OrderStatus.Created);
        return Result.success(order);
    }

    public UnitResult<Error> assign() {
        if (status != OrderStatus.Created) {
            return UnitResult.failure(Error.of("not_created", "Заказ не в статусе Создан"));
        }
        this.status = OrderStatus.Assigned;
        return UnitResult.success();

    }

    public UnitResult<Error> complete() {
        if (status != OrderStatus.Assigned) {
            return UnitResult.failure(Error.of("not_assigned", "Заказ не присвоен курьеру"));
        }
        this.status = OrderStatus.Completed;
        return UnitResult.success();

    }
}
