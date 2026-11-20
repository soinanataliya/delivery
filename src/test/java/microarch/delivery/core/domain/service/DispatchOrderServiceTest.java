package microarch.delivery.core.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.domain.services.DispatchOrderServiceImpl;

public class DispatchOrderServiceTest {

    private final DispatchOrderServiceImpl service = new DispatchOrderServiceImpl();

    @Test
    public void shouldAssignOrderToNearestAvailableCourier() {
        var orderLocation = Location.create(1, 1).getValueOrThrow();
        var order = Order.create(UUID.randomUUID(), Volume.create(5).getValueOrThrow(), orderLocation)
                .getValueOrThrow();

        var fullCourier = Courier.create("Загруженный", orderLocation).getValueOrThrow();

        var nearestAvailableCourier = Courier.create("Ближайший свободный", Location.create(2, 1).getValueOrThrow())
                .getValueOrThrow();

        var distantCourier = Courier.create("Дальний", Location.create(5, 5).getValueOrThrow()).getValueOrThrow();

        var existingOrder = Order.create(UUID.randomUUID(), Volume.create(20).getValueOrThrow(), orderLocation)
                .getValueOrThrow();

        assertThat(fullCourier.assignOrder(existingOrder).isSuccess()).isTrue();
        assertThat(existingOrder.assign().isSuccess()).isTrue();

        var result = service.dispatch(order, new Courier[] { distantCourier, fullCourier, nearestAvailableCourier });

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue()).isSameAs(nearestAvailableCourier);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Assigned);

        var winnerAssignments = nearestAvailableCourier.getAssignments();
        assertThat(winnerAssignments).hasSize(1);
        assertThat(winnerAssignments.get(0).getOrderId()).isEqualTo(order.getId());

        var fullCourierAssignments = fullCourier.getAssignments();
        assertThat(fullCourierAssignments).hasSize(1);
        assertThat(fullCourierAssignments.get(0).getOrderId()).isEqualTo(existingOrder.getId());

        assertThat(distantCourier.getAssignments()).isEmpty();
    }

    @Test
    public void shouldReturnErrorWhenAllCouriersAreFull() {
        var location = Location.create(1, 1).getValueOrThrow();
        var order = Order.create(UUID.randomUUID(), Volume.create(5).getValueOrThrow(), location).getValueOrThrow();

        var firstCourier = Courier.create("Первый", location).getValueOrThrow();
        var secondCourier = Courier.create("Второй", location).getValueOrThrow();

        var firstExistingOrder = Order.create(UUID.randomUUID(), Volume.create(20).getValueOrThrow(), location)
                .getValueOrThrow();

        var secondExistingOrder = Order.create(UUID.randomUUID(), Volume.create(20).getValueOrThrow(), location)
                .getValueOrThrow();

        assertThat(firstCourier.assignOrder(firstExistingOrder).isSuccess()).isTrue();
        assertThat(firstExistingOrder.assign().isSuccess()).isTrue();

        assertThat(secondCourier.assignOrder(secondExistingOrder).isSuccess()).isTrue();
        assertThat(secondExistingOrder.assign().isSuccess()).isTrue();

        var result = service.dispatch(order, new Courier[] { firstCourier, secondCourier });

        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("courier_not_found");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Created);

        var firstCourierAssignments = firstCourier.getAssignments();
        assertThat(firstCourierAssignments).hasSize(1);
        assertThat(firstCourierAssignments.get(0).getOrderId()).isEqualTo(firstExistingOrder.getId());

        var secondCourierAssignments = secondCourier.getAssignments();
        assertThat(secondCourierAssignments).hasSize(1);
        assertThat(secondCourierAssignments.get(0).getOrderId()).isEqualTo(secondExistingOrder.getId());
    }
}
