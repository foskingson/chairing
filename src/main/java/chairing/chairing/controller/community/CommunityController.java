package chairing.chairing.controller.community;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import chairing.chairing.domain.community.Post;
import chairing.chairing.dto.community.CommentRequest;
import chairing.chairing.dto.community.PostRequest;
import chairing.chairing.service.community.CommentService;
import chairing.chairing.service.community.PostService;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final PostService postService;
    private final CommentService commentService;

    // 게시물 목록 반환 (JSON)
    @GetMapping
    public ResponseEntity<List<Post>> listPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 게시물 상세 보기
    @GetMapping("/post/{id}")
    public ResponseEntity<Post> viewPost(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    // 게시물 생성 (POST)
    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(@RequestBody PostRequest postRequest, Principal principal) {
        postService.createPost(principal, postRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post created successfully");
    }

    // 댓글 생성
    @PostMapping("/post/{id}/comment")
    public ResponseEntity<?> createComment(@PathVariable Long id, @RequestBody CommentRequest commentRequest, Principal principal) {
        commentRequest.setPostId(id);
        commentService.createComment(principal, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Comment added successfully");
    }

    // 게시물 삭제
    @DeleteMapping("/post/{id}/delete")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.ok("Post deleted successfully");
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{id}/delete")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}
