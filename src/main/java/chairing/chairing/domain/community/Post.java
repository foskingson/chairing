package chairing.chairing.domain.community;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import chairing.chairing.domain.user.User;

@Entity
@Data
@NoArgsConstructor
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @JsonManagedReference 
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

    @OneToMany(mappedBy = "post", cascade = {CascadeType.ALL, CascadeType.REMOVE}, orphanRemoval = true)
    @JsonManagedReference 
    private List<Comment> comments; 


    public Post(User user, String title, String content, String imageUrl) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createDate = LocalDateTime.now();
        this.expireDate = this.createDate.plusDays(1); 
    }
}