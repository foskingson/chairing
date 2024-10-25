package chairing.chairing.repository.wheelchair;

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
    Optional<Wheelchair> findFirstByTypeAndStatus(WheelchairType type, WheelchairStatus status);
    
    
     // 휠체어의 수를 가져오는 메서드
    @Query("SELECT COUNT(w) FROM Wheelchair w WHERE w.type = :type AND w.status = :status")
    long countByTypeAndStatus(@Param("type") WheelchairType type, @Param("status") WheelchairStatus status);
}