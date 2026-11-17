package microarch.delivery.core.domain.model.courier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Status;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;

public class CourierTest {
    @Test
    public void shouldRejectInvalidNames() {
        var location = Location.create(3, 3).getValue();

        for (String name : new String[] { null, new String(""), "   " }) {
            var result = Courier.create(name, location);

            assertThat(result.isFailure()).isTrue();
        }
    }

    @Test
    public void shouldRejectNullLocation() {
        var result = Courier.create("Иванов Иван", null);

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    public void shouldNotAllowChangingAssignmentsThroughGetter() {
        var location = Location.create(3, 3).getValue();
        var courier = Courier.create("Иванов Иван", location).getValue();
        var volume = Volume.create(2).getValue();
        var assignment = Assignment.create(UUID.randomUUID(), volume, location).getValue();

        assertThatThrownBy(() -> courier.getAssignments().add(assignment))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(courier.getAssignments()).isEmpty();
    }

    @Test
    public void shouldReturnSuccessWhenCreated() {

        String name = "Иванов Иван";
        var maxVolume = Volume.create(20).getValue();
        var location = Location.create(3, 3).getValue();
        var courier = Courier.create(name, location).getValue();

        assertThat(courier).isNotNull();
        assertThat(courier.getMaxVolume()).isEqualTo(maxVolume);
        assertThat(courier.getLocation()).isEqualTo(location);
        assertThat(courier.getAssignments()).isEmpty();
    }

    @Test
    public void shouldAllowOrderWhenTotalVolumeEqualsMaxVolume() {
        var location = Location.create(3, 3).getValue();

        var courier = Courier.create("Иванов Иван", location).getValue();

        var orderVolume = Volume.create(2).getValue();

        var order = Order.create(UUID.randomUUID(), orderVolume, location).getValue();

        var assignResult = courier.assignOrder(order);

        assertThat(assignResult.isSuccess()).isTrue();

        var newOrderVolume = Volume.create(18).getValue();

        var result = courier.canTakeOrder(newOrderVolume);
        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getAssignments()).hasSize(1);
    }

    @Test
    public void shouldRejectOrderWhenTotalVolumeExceedsMaxVolume() {
        var location = Location.create(3, 3).getValue();

        var courier = Courier.create("Иванов Иван", location).getValue();

        var orderVolume = Volume.create(2).getValue();

        var order = Order.create(UUID.randomUUID(), orderVolume, location).getValue();

        var assignResult = courier.assignOrder(order);

        assertThat(assignResult.isSuccess()).isTrue();

        var newOrderVolume = Volume.create(19).getValue();

        var result = courier.canTakeOrder(newOrderVolume);
        assertThat(result.isFailure()).isTrue();
        assertThat(courier.getAssignments()).hasSize(1);
    }

    @Test
    public void shouldCreateAssignmentWithOrderData() {
        var courierLocation = Location.create(3, 3).getValue();

        var courier = Courier.create("Иванов Иван", courierLocation).getValue();

        var orderId = UUID.randomUUID();
        var orderVolume = Volume.create(2).getValue();
        var orderLocation = Location.create(4, 3).getValue();

        var order = Order.create(orderId, orderVolume, orderLocation).getValue();

        var result = courier.assignOrder(order);

        assertThat(result.isSuccess()).isTrue();
        assertThat(courier.getAssignments()).hasSize(1);

        var assignment = courier.getAssignments().get(0);
        assertThat(assignment.getOrderId()).isEqualTo(orderId);
        assertThat(assignment.getVolume()).isEqualTo(orderVolume);
        assertThat(assignment.getLocation()).isEqualTo(orderLocation);
        assertThat(assignment.getStatus()).isEqualTo(Status.Assigned);
    }

    @Test
    public void shouldNotAddAssignmentWhenCapacityIsExceeded() {
        var location = Location.create(3, 3).getValue();

        var courier = Courier.create("Иванов Иван", location).getValue();

        var orderVolume = Volume.create(11).getValue();

        var firstOrder = Order.create(UUID.randomUUID(), orderVolume, location).getValue();

        var firstResult = courier.assignOrder(firstOrder);

        assertThat(firstResult.isSuccess()).isTrue();
        assertThat(courier.getAssignments()).hasSize(1);

        var firstAssignment = courier.getAssignments().get(0);
        var secondOrder = Order.create(UUID.randomUUID(), orderVolume, location).getValue();

        var result = courier.assignOrder(secondOrder);
        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("volume_exceeded");
        assertThat(courier.getAssignments()).containsExactly(firstAssignment);
    }

    @Test
    public void shouldCompleteAssignmentWhenOrderIsOneCellAway() {
        var courierLocation = Location.create(3, 3).getValue();

        var courier = Courier.create("Иванов Иван", courierLocation).getValue();

        var orderVolume = Volume.create(2).getValue();
        var orderLocation = Location.create(3, 4).getValue();

        var order = Order.create(UUID.randomUUID(), orderVolume, orderLocation).getValue();

        var assignResult = courier.assignOrder(order);

        assertThat(assignResult.isSuccess()).isTrue();

        var assignment = courier.getAssignments().get(0);

        var result = courier.completeAssignment(assignment.getId());

        assertThat(result.isSuccess()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(Status.Completed);
    }

    @Test
    public void shouldNotCompleteAssignmentWhenOrderIsTwoCellsAway() {
        var courierLocation = Location.create(3, 3).getValue();

        var courier = Courier.create("Иванов Иван", courierLocation).getValue();

        var orderVolume = Volume.create(2).getValue();
        var orderLocation = Location.create(3, 5).getValue();

        var order = Order.create(UUID.randomUUID(), orderVolume, orderLocation).getValue();

        var assignResult = courier.assignOrder(order);

        assertThat(assignResult.isSuccess()).isTrue();

        var assignment = courier.getAssignments().get(0);

        var result = courier.completeAssignment(assignment.getId());

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("courier_too_far");
        assertThat(assignment.getStatus()).isEqualTo(Status.Assigned);
    }
}
