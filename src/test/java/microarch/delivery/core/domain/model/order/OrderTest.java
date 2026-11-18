package microarch.delivery.core.domain.model.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;

public class OrderTest {

    @Test
    public void shouldCreateOrderWithProvidedDataAndCreatedStatus() {
        var id = UUID.randomUUID();
        var volume = Volume.create(2).getValue();
        var location = Location.create(3, 3).getValue();

        var result = Order.create(id, volume, location);

        assertThat(result.isSuccess()).isTrue();
        var order = result.getValue();
        assertThat(order.getId()).isEqualTo(id);
        assertThat(order.getVolume()).isEqualTo(volume);
        assertThat(order.getLocation()).isEqualTo(location);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Created);
    }

    @Test
    public void shouldReturnFailureWhenIdIsNull() {
        var volume = Volume.create(2).getValue();
        var location = Location.create(3, 3).getValue();

        var result = Order.create(null, volume, location);

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    public void shouldReturnFailureWhenVolumeIsNull() {
        var id = UUID.randomUUID();
        var location = Location.create(3, 3).getValue();

        var result = Order.create(id, null, location);

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    public void shouldReturnFailureWhenLocationIsNull() {
        var id = UUID.randomUUID();
        var volume = Volume.create(2).getValue();

        var result = Order.create(id, volume, null);

        assertThat(result.isFailure()).isTrue();
    }

    @Test
    public void shouldAssignCreatedOrder() {
        var id = UUID.randomUUID();
        var volume = Volume.create(2).getValue();
        var location = Location.create(3, 3).getValue();
        var order = Order.create(id, volume, location).getValue();

        var result = order.assign();

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Assigned);
    }

    @Test
    public void shouldNotAssignOrderTwice() {
        var id = UUID.randomUUID();
        var volume = Volume.create(2).getValue();
        var location = Location.create(3, 3).getValue();
        var order = Order.create(id, volume, location).getValue();
        assertThat(order.assign().isSuccess()).isTrue();

        var result = order.assign();

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("not_created");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Assigned);
    }

    @Test
    public void shouldNotAssignCompletedOrder() {
        var id = UUID.randomUUID();
        var volume = Volume.create(2).getValue();
        var location = Location.create(3, 3).getValue();
        var order = Order.create(id, volume, location).getValue();
        assertThat(order.assign().isSuccess()).isTrue();
        assertThat(order.complete().isSuccess()).isTrue();

        var result = order.assign();

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("not_created");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Completed);
    }

    @Test
    public void shouldCompleteAssignedOrder() {
        var id = UUID.randomUUID();
        var volume = Volume.create(2).getValue();
        var location = Location.create(3, 3).getValue();
        var order = Order.create(id, volume, location).getValue();
        assertThat(order.assign().isSuccess()).isTrue();

        var result = order.complete();

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Completed);
    }

    @Test
    public void shouldNotCompleteCreatedOrder() {
        var id = UUID.randomUUID();
        var volume = Volume.create(2).getValue();
        var location = Location.create(3, 3).getValue();
        var order = Order.create(id, volume, location).getValue();

        var result = order.complete();

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("not_assigned");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Created);
    }

    @Test
    public void shouldNotCompleteOrderTwice() {
        var id = UUID.randomUUID();
        var volume = Volume.create(2).getValue();
        var location = Location.create(3, 3).getValue();
        var order = Order.create(id, volume, location).getValue();
        assertThat(order.assign().isSuccess()).isTrue();
        assertThat(order.complete().isSuccess()).isTrue();

        var result = order.complete();

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("not_assigned");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.Completed);
    }
}
