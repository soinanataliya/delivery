package microarch.delivery.adapters.out.postgres;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import microarch.delivery.core.ports.CourierRepository;
import microarch.delivery.core.ports.OrderRepository;

@Repository
public class CourierRepositoryImpl implements CourierRepository {
    private final CourierJpaRepository jpa;

    public CourierRepositoryImpl(CourierJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void add(Courier courier) {
        jpa.save(courier);
    }

    @Override
    public void update(Courier courier) {
        jpa.save(courier);
    }

    @Override
    public Optional<Courier> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public List<Courier> findAll() {
        return jpa.findAll();
    }
}