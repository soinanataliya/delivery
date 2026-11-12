package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VolumeTest {

    @Test
    public void shouldReturnSuccessWhenValueIsValid() {
        var volume = Volume.create(5);

        assertThat(volume.isSuccess()).isTrue();
        assertThat(volume.getValue().getVolume()).isEqualTo(5);
    }

    @Test
    public void shouldReturnErrorWhenValueIsZero() {
        var volume = Volume.create(0);

        assertThat(volume.isSuccess()).isFalse();
    }

    @Test
    public void shouldReturnErrorWhenValueIsNegative() {
        var volume = Volume.create(-1);

        assertThat(volume.isSuccess()).isFalse();
    }

    @Test
    public void shouldBeEqualWhenTwoVolumesAreTheSame() {
        var first = Volume.create(5).getValue();
        var second = Volume.create(5).getValue();

        var result = first.equals(second);
        assertThat(result).isTrue();
    }

    @Test
    public void shouldNotBeEqualWhenTwoVolumesAreDifferent() {
        var first = Volume.create(5).getValue();
        var second = Volume.create(7).getValue();

        var result = first.equals(second);
        assertThat(result).isFalse();
    }
}