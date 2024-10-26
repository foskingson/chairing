package chairing.chairing.domain.community;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import chairing.chairing.domain.user.User;

@Entity
@Getter
@Setter // 테스트 용도
@NoArgsConstructor
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @JsonManagedReference // 추가
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime createDate;

    @Column(nullable = false)
    private LocalDateTime expireDate;

    // 댓글과의 관계 설정: 
    // Post가 삭제될 때 관련된 댓글도 함께 삭제되도록 cascade 옵션 추가
    @OneToMany(mappedBy = "post", cascade = {CascadeType.ALL, CascadeType.REMOVE}, orphanRemoval = true)
    @JsonManagedReference // 추가
    private List<Comment> comments; // 추가된 부분


    public Post(User user, String title, String content, String imageUrl) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createDate = LocalDateTime.now();
        this.expireDate = this.createDate.plusDays(1); // 하루 후 삭제
    }
}