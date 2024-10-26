package chairing.chairing.repository.delivery;

import chairing.chairing.domain.rental.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import chairing.chairing.domain.delivery.Delivery;

import java.util.Optional;


@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByRental(Rental rental);
}
