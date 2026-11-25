package microarch.delivery.adapters.out.postgres;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import microarch.delivery.core.domain.model.courier.Courier;

public interface CourierJpaRepository extends JpaRepository<Courier, UUID> {
    List<Courier> findAll();
}
