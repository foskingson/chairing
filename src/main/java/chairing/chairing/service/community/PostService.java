package chairing.chairing.service.community;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import chairing.chairing.domain.community.Post;
import chairing.chairing.domain.user.User;
import chairing.chairing.dto.community.PostRequest;
import chairing.chairing.repository.community.PostRepository;
import chairing.chairing.repository.user.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;

    // 외부 경로로 변경: D:/project/img/
    private final String UPLOAD_DIR = "D:/project/img/";

    public void createPost(User user, PostRequest postRequest) {
        String imageUrl = null; // 기본적으로 null로 설정
        if (postRequest.getImageUrl() != null && !postRequest.getImageUrl().isEmpty()) {
            imageUrl = saveFile(postRequest.getImageUrl()); // 파일이 있는 경우 저장
        }

        // Post 객체 생성
        Post post = new Post(user, postRequest.getTitle(), postRequest.getContent(), imageUrl);
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    // 매일 자정에 만료된 게시글 삭제
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredPosts() {
        List<Post> expiredPosts = postRepository.findAllByExpireDateBefore(LocalDateTime.now());
        postRepository.deleteAll(expiredPosts);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    private String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            // 파일 이름과 확장자 분리
            String originalFilename = file.getOriginalFilename();
            String fileNameWithoutExt = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 파일 저장 경로 설정
            String newFileName = originalFilename;
            Path path = Paths.get(UPLOAD_DIR + newFileName);

            // 파일 이름 중복 처리
            int count = 1;
            while (Files.exists(path)) {
                newFileName = fileNameWithoutExt + "_" + count + extension;
                path = Paths.get(UPLOAD_DIR + newFileName);
                System.out.println("중복 파일 발견: " + newFileName);
                count++;
            }

            // 디렉토리 생성
            Files.createDirectories(path.getParent());
            // 파일 복사
            Files.copy(file.getInputStream(), path);
            System.out.println("저장된 파일 이름: " + newFileName);

            // 저장된 파일의 URL 반환 (ResourceHandler로 노출된 경로 사용)
            return "/img/" + newFileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.");
        }
    }

    public void deleteImg(Long id) {
        // 게시글 조회
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    
        // 이미지 경로 가져오기
        String imageUrl = post.getImageUrl();
    
        // 이미지 파일 삭제
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                // 이미지 URL에서 파일명만 추출 (예: "/img/filename.png"에서 "filename.png")
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    
                // 파일 경로 생성
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
    
                // 파일이 존재하는지 확인 후 삭제
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                    System.out.println("이미지 파일 삭제: " + filePath);
                } else {
                    System.out.println("이미지 파일이 존재하지 않습니다: " + filePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("이미지 파일 삭제 중 오류가 발생했습니다.");
            }
        }
    }    
}
