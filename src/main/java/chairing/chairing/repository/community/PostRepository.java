package chairing.chairing.repository.community;

import chairing.chairing.domain.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByExpireDateBefore(LocalDateTime now);
}