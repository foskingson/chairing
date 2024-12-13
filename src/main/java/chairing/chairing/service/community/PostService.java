package chairing.chairing.service.community;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import chairing.chairing.domain.community.Post;
import chairing.chairing.domain.user.User;
import chairing.chairing.dto.community.PostRequest;
import chairing.chairing.repository.community.PostRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;

    @Value("${upload.dir}")
    private final String UPLOAD_DIR;

    public void createPost(User user, PostRequest postRequest) {
        String imageUrl = null; 
        if (postRequest.getImageUrl() != null && !postRequest.getImageUrl().isEmpty()) {
            imageUrl = saveFile(postRequest.getImageUrl());
        }

        Post post = new Post(user, postRequest.getTitle(), postRequest.getContent(), imageUrl);
        postRepository.save(post);
    }

    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

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
            String originalFilename = file.getOriginalFilename();
            String fileNameWithoutExt = originalFilename.substring(0, originalFilename.lastIndexOf("."));
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            String newFileName = originalFilename;
            Path path = Paths.get(UPLOAD_DIR + newFileName);

            int count = 1;
            while (Files.exists(path)) {
                newFileName = fileNameWithoutExt + "_" + count + extension;
                path = Paths.get(UPLOAD_DIR + newFileName);
                System.out.println("중복 파일 발견: " + newFileName);
                count++;
            }

            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path);
            System.out.println("저장된 파일 이름: " + newFileName);

            return "/img/" + newFileName;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.");
        }
    }

    public void deleteImg(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    
        String imageUrl = post.getImageUrl();
    
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
    
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
