package microarch.delivery.adapters.out.postgres;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.ports.CourierRepository;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CourierRepositoryIntegrationTest extends PostgresIntegrationTestBase {

    @Autowired
    CourierRepository repository;

    private Courier createCourier() {
        var location = Location.create(1, 2).getValue();
        return Courier.create("Иван", location).getValue();
    }

    @Test
    void CanAddAndFindCourierById() {
        // Arrange
        var courier = createCourier();

        // Act
        repository.add(courier);
        var loaded = repository.findById(courier.getId());

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(courier.getId());
        assertThat(loaded.get().getName()).isEqualTo(courier.getName());
    }

    @Test
    void CanFindAllCouriers() {
        // Arrange
        var first = createCourier();
        var second = createCourier();

        // Act
        repository.add(first);
        repository.add(second);
        var all = repository.findAll();

        // Assert
        assertThat(all).extracting(currCourier -> currCourier.getId()).contains(first.getId(), second.getId());
    }
}