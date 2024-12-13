package chairing.chairing.repository.rental;

import chairing.chairing.domain.rental.RentalStatus;
import org.springframework.stereotype.Repository;

import chairing.chairing.domain.rental.Rental;
import chairing.chairing.domain.user.User;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    Optional<Rental> findByRentalIdAndUser(Long rentalId, User user);

    @Query("SELECT r FROM Rental r WHERE r.user = ?1 AND r.status = chairing.chairing.domain.rental.RentalStatus.ACTIVE")
    Optional<Rental> findCurrentRentalByUser(User user);

    @Query("SELECT r FROM Rental r WHERE r.user = ?1 AND (r.status = chairing.chairing.domain.rental.RentalStatus.ACTIVE OR r.status = chairing.chairing.domain.rental.RentalStatus.WAITING)")
    Optional<Rental> findCurrentRentalByUserV2(User user);

    List<Rental> findByUser(User user);

    List<Rental> findByStatus(RentalStatus status); 
    int countByRentalCode(String guardianCode);

    Optional<Rental> findByRentalCode(String guardianCode);
}