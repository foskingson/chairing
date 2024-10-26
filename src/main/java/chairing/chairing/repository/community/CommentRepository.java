package chairing.chairing.repository.community;
import chairing.chairing.domain.community.Comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.postId = :postId")
    List<Comment> findByPostIdWithUser(@Param("postId") Long postId);

    
}