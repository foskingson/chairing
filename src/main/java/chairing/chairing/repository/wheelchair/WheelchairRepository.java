package chairing.chairing.repository.wheelchair;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import chairing.chairing.domain.wheelchair.Wheelchair;
import chairing.chairing.domain.wheelchair.WheelchairStatus;
import chairing.chairing.domain.wheelchair.WheelchairType;


@Repository
public interface WheelchairRepository extends JpaRepository<Wheelchair, Long> {
    long countByStatus(String status);

    int countByStatus(WheelchairStatus status);

    Optional<Wheelchair> findFirstByTypeAndStatus(WheelchairType type, WheelchairStatus status);

    List<Wheelchair> findByStatus(WheelchairStatus status);
    
    
    @Query("SELECT COUNT(w) FROM Wheelchair w WHERE w.type = :type AND w.status = :status")
    long countByTypeAndStatus(@Param("type") WheelchairType type, @Param("status") WheelchairStatus status);
}