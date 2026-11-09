package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LocationTest {

    @Test
    public void shouldReturnSuccessWhenCoordsAreValid() {
        var location = Location.create(1, 1);
        assertThat(location.isSuccess()).isTrue();
    }

    @Test
    public void shouldReturnErrorWhenCoordsAreInvalid() {
        var location = Location.create(0, 0);
        assertThat(location.isSuccess()).isFalse();
    }

    @Test
    public void shouldBeEqualWhenTwoCoordinatesAreTheSame() {
        var first = Location.create(1, 1).getValueOrThrow();
        var second = Location.create(1, 1).getValueOrThrow();

        var result = first.equals(second);
        assertThat(result).isTrue();
    }

    @Test
    public void shouldNotBeEqualWhenTwoCoordinatesAreDifferent() {
        var first = Location.create(1, 2).getValue();
        var second = Location.create(3, 1).getValue();

        var result = first.equals(second);
        assertThat(result).isFalse();
    }

    @Test
    public void shouldCalculateDistanceBetweenTwoLocations() {
        var first = Location.create(1, 1).getValue();
        var second = Location.create(3, 4).getValue();

        var result = first.distanceTo(second);
        assertThat(result).isEqualTo(5);
    }
}
