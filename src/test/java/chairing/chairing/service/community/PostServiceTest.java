// package chairing.chairing.service.community;

// import chairing.chairing.domain.community.Post;
// import chairing.chairing.domain.user.User;
// import chairing.chairing.dto.community.PostRequest;
// import chairing.chairing.repository.community.PostRepository;
// import chairing.chairing.repository.user.UserRepository;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.transaction.annotation.Transactional;

// import java.security.Principal;
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyLong;
// import static org.mockito.Mockito.*;

// @Transactional
// public class PostServiceTest {

//     @InjectMocks
//     private PostService postService;

//     @Mock
//     private PostRepository postRepository;

//     @Mock
//     private UserRepository userRepository;

//     @BeforeEach
//     public void setUp() {
//         MockitoAnnotations.openMocks(this);
//     }

//     @Test
//     public void testCreatePost() {
//         User user = new User(); // 적절한 User 객체로 설정
//         user.setUsername("testuser");
        
//         PostRequest request = new PostRequest("title", "content", "imageUrl");
//         Principal principal = mock(Principal.class);
//         when(principal.getName()).thenReturn("testuser");
//         when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
//         when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

//         Post post = postService.createPost(principal, request);

//         verify(userRepository, times(1)).findByUsername("testuser");
//         verify(postRepository, times(1)).save(any(Post.class));
//         assert post.getTitle().equals("title");
//         assert post.getContent().equals("content");
//     }

//     @Test
//     public void testDeletePost() {
//         Long postId = 1L;

//         postService.deletePost(postId);

//         verify(postRepository, times(1)).deleteById(postId);
//     }

//     @Test
//     public void testDeleteExpiredPosts() {
//         LocalDateTime now = LocalDateTime.now();
//         Post expiredPost = new Post(new User(), "title", "content", "imageUrl");
//         expiredPost.setExpireDate(now.minusDays(1)); // 이미 만료된 게시물
//         when(postRepository.findAllByExpireDateBefore(any(LocalDateTime.class)))
//                 .thenReturn(List.of(expiredPost));

//         postService.deleteExpiredPosts();

//         verify(postRepository, times(1)).deleteAll(List.of(expiredPost));
//     }

//     @Test
//     public void testGetAllPosts() {
//         Post post1 = new Post(new User(), "title1", "content1", "imageUrl1");
//         Post post2 = new Post(new User(), "title2", "content2", "imageUrl2");
//         when(postRepository.findAll()).thenReturn(List.of(post1, post2));

//         List<Post> posts = postService.getAllPosts();

//         verify(postRepository, times(1)).findAll();
//         assert posts.size() == 2;
//     }

//     @Test
//     public void testGetPostById() {
//         Long postId = 1L;
//         Post post = new Post(new User(), "title", "content", "imageUrl");
//         when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

//         Post foundPost = postService.getPostById(postId);

//         verify(postRepository, times(1)).findById(postId);
//         assert foundPost.equals(post);
//     }
// }
