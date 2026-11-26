package microarch.delivery.adapters.out.postgres;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.Volume;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.OrderRepository;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderRepositoryIntegrationTest extends PostgresIntegrationTestBase {

    @Autowired
    OrderRepository repository;

    private Order createOrder() {
        var id = UUID.randomUUID();
        var volume = Volume.create(2).getValue();
        var location = Location.create(3, 3).getValue();
        return Order.create(id, volume, location).getValue();
    }

    @Test
    void CanAddAndFindOrderById() {
        // Arrange
        var order = createOrder();

        // Act
        repository.add(order);
        var loaded = repository.findById(order.getId());

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(order.getId());
        assertThat(loaded.get().getStatus()).isEqualTo(OrderStatus.Created);
        assertThat(loaded.get().getVolume()).isEqualTo(order.getVolume());
        assertThat(loaded.get().getLocation()).isEqualTo(order.getLocation());
    }

    @Test
    void CanFindAnyCreatedOrder() {
        // Arrange
        var order = createOrder();

        // Act
        repository.add(order);
        var loaded = repository.findAnyCreated();

        // Assert
        assertThat(loaded).isPresent();
        assertThat(loaded.get().getId()).isEqualTo(order.getId());
    }

    @Test
    void CanFindAssignedOrdersAfterAssign() {
        // Arrange
        var order = createOrder();
        repository.add(order);
        var loaded = repository.findById(order.getId()).orElseThrow();
        assertThat(loaded.getStatus()).isEqualTo(OrderStatus.Created);

        // Act
        assertThat(loaded.assign().isSuccess()).isTrue();
        repository.update(loaded);
        var updated = repository.findById(order.getId()).orElseThrow();
        var assigned = repository.findAllAssigned();

        // Assert
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.Assigned);
        assertThat(assigned).extracting(currOrder -> currOrder.getId()).contains(order.getId());
    }
}
