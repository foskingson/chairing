package chairing.chairing.repository.user;

import org.springframework.stereotype.Repository;

import chairing.chairing.domain.user.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.rentals WHERE u.username = :username")
    User findByUsernameWithRentals(@Param("username") String username);
}