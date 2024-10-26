package chairing.chairing.domain.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import chairing.chairing.domain.rental.Rental;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter // 테스트 용도
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Rental> rentals;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;


    private String guardianCode = "0"; // 디폴트 설정 

    public User() {
        // 기본 생성자
    }
    

    public User(String username, String password, String phoneNumber, UserRole role, String guardianCode) {
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.guardianCode = guardianCode;
    }
}