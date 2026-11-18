package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class VolumeTest {

    @Test
    public void shouldReturnTrueWhenVolumeIsGreater() {
        var first = Volume.create(21).getValue();
        var second = Volume.create(20).getValue();

        assertThat(first.greaterThan(second)).isTrue();
    }

    @Test
    public void shouldReturnFalseWhenVolumesAreEqual() {
        var first = Volume.create(20).getValue();
        var second = Volume.create(20).getValue();

        assertThat(first.greaterThan(second)).isFalse();
    }

    @Test
    public void shouldReturnFalseWhenVolumeIsLess() {
        var first = Volume.create(19).getValue();
        var second = Volume.create(20).getValue();

        assertThat(first.greaterThan(second)).isFalse();
    }

    @Test
    public void shouldRejectNullWhenComparingVolumes() {
        var volume = Volume.create(20).getValue();

        assertThatThrownBy(() -> volume.greaterThan(null))
                .isInstanceOf(NullPointerException.class);
    }

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

    @Test
    public void shouldAddVolumesWithoutChangingOriginalValues() {
        var first = Volume.create(2).getValue();
        var second = Volume.create(3).getValue();

        var result = first.add(second);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getValue().getVolume()).isEqualTo(5);

        assertThat(first.getVolume()).isEqualTo(2);
        assertThat(second.getVolume()).isEqualTo(3);
    }

    @Test
    public void shouldReturnFailureWhenAddedVolumeIsNull() {
        var volume = Volume.create(2).getValue();

        var result = volume.add(null);

        assertThat(result.isFailure()).isTrue();
        assertThat(volume.getVolume()).isEqualTo(2);
    }

    @Test
    public void shouldReturnFailureWhenVolumeSumOverflows() {
        var first = Volume.create(Integer.MAX_VALUE).getValue();
        var second = Volume.create(1).getValue();

        var result = first.add(second);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().getCode()).isEqualTo("volume_overflow");

        assertThat(first.getVolume()).isEqualTo(Integer.MAX_VALUE);
        assertThat(second.getVolume()).isEqualTo(1);
    }
}
