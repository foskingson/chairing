package chairing.chairing.controller.community;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import chairing.chairing.config.JwtUtil;
import chairing.chairing.domain.community.Comment;
import chairing.chairing.domain.community.Post;
import chairing.chairing.domain.user.User;
import chairing.chairing.dto.community.CommentRequest;
import chairing.chairing.dto.community.PostRequest;
import chairing.chairing.service.community.CommentService;
import chairing.chairing.service.community.PostService;
import chairing.chairing.service.user.UserService;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final PostService postService;
    private final CommentService commentService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    // 게시물 목록 반환 (JSON)
    @GetMapping
    public ResponseEntity<List<Post>> listPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 게시물 상세 보기
    @GetMapping("/post/{postId}")
    public ResponseEntity<Post> viewPost(@PathVariable("postId") Long id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    // 게시물 생성 (POST)
    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(@ModelAttribute PostRequest postRequest, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // "Bearer " 제거
        Long userId = jwtUtil.getUserIdFromToken(token);
        System.out.println(userId);
        User user = userService.getCurrentUser(userId);
        try {
            postService.createPost(user, postRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("Post created successfully");
        } catch (Exception e) {
            // 에러 메시지 로그
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating post: " + e.getMessage());
        }
    }

    @GetMapping("/post/{postId}/comments")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable("postId") Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 생성
    @PostMapping("/post/{postId}/comment")
    public ResponseEntity<?> createComment(@PathVariable("postId") Long id, @RequestBody CommentRequest commentRequest, Principal principal) {
        commentRequest.setPostId(id);
        commentService.createComment(principal, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Comment added successfully");
    }

    // 게시물 삭제
    @DeleteMapping("/post/{id}/delete")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deleteImg(id);
        postService.deletePost(id);
        return ResponseEntity.ok("Post deleted successfully");
    }

    // 댓글 삭제
    @DeleteMapping("/post/{postId}/comment/{commentId}/delete")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting comment: " + e.getMessage());
        }
    }
}
