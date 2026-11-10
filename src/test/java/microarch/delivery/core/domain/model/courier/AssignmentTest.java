package microarch.delivery.core.domain.model.courier;

import org.junit.jupiter.api.Test;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Status;
import microarch.delivery.core.domain.model.Volume;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AssignmentTest {

    @Test
    public void shouldReturnSuccessWhenCreated() {
        var orderId = UUID.randomUUID();
        var volume = Volume.create(5).getValue();
        var location = Location.create(3, 3).getValue();

        var assignment = Assignment.create(orderId, volume, location).getValue();

        assertThat(assignment).isNotNull();
        assertThat(assignment.getOrderId()).isEqualTo(orderId);
        assertThat(assignment.getVolume()).isEqualTo(volume);
        assertThat(assignment.getLocation()).isEqualTo(location);
        assertThat(assignment.getStatus()).isEqualTo(Status.Assigned);
    }

    @Test
    public void shouldCompleteWhenCourierIsAtOrderLocation() {
        var orderLocation = Location.create(3, 3).getValue();
        var assignment = Assignment.create(UUID.randomUUID(), Volume.create(5).getValue(), orderLocation).getValue();

        var result = assignment.complete(orderLocation);

        assertThat(result.isSuccess()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(Status.Completed);
    }

    @Test
    public void shouldCompleteWhenCourierIsOneCellAway() {
        var assignment = Assignment
                .create(UUID.randomUUID(), Volume.create(5).getValue(), Location.create(3, 3).getValue()).getValue();
        var courierLocation = Location.create(4, 3).getValue();

        var result = assignment.complete(courierLocation);

        assertThat(result.isSuccess()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(Status.Completed);
    }

    @Test
    public void shouldReturnErrorWhenCourierIsTwoCellsAway() {
        var assignment = Assignment
                .create(UUID.randomUUID(), Volume.create(5).getValue(), Location.create(3, 3).getValue()).getValue();
        var courierLocation = Location.create(5, 3).getValue();

        var result = assignment.complete(courierLocation);

        assertThat(result.isFailure()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(Status.Assigned);
    }

    @Test
    public void shouldReturnErrorWhenCourierLocationIsNull() {
        var assignment = Assignment
                .create(UUID.randomUUID(), Volume.create(5).getValue(), Location.create(3, 3).getValue()).getValue();

        var result = assignment.complete(null);

        assertThat(result.isFailure()).isTrue();
        assertThat(assignment.getStatus()).isEqualTo(Status.Assigned);
    }
}