package chairing.chairing.service.user;

import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chairing.chairing.domain.user.User;
import chairing.chairing.domain.user.UserRole;
import chairing.chairing.dto.user.LoginRequest;
import chairing.chairing.dto.user.UserCreateRequest;
import chairing.chairing.repository.rental.RentalRepository;
import chairing.chairing.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

// 공통 로직 회원가입 같은 것들 모음

@RequiredArgsConstructor
@Service
public class UserService {
        
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RentalRepository rentalRepository;

    @Transactional
    public User getCurrentUser(Long userId) {
        // userId를 통해 User 정보를 가져오고, 대여 정보도 포함하여 반환
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    @Transactional
    public User create(UserCreateRequest request) {
        User user = new User(
            request.getUsername(),
            passwordEncoder.encode(request.getPassword()),
            request.getPhoneNumber(),
            request.getRole(),
            request.getGuardianCode()
        );

        if (user.getRole()==UserRole.GUARDIAN && rentalRepository.countByRentalCode(user.getGuardianCode()) < 1) {
            throw new IllegalArgumentException("해당 대여 코드를 찾을 수 없습니다.");
        }

        this.userRepository.save(user);
        return user;
    }

    @Transactional
    public Authentication authenticate(LoginRequest loginRequest) {
        // UsernamePasswordAuthenticationToken을 사용하여 인증 시도
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        System.out.println("Authentication: " + authentication.isAuthenticated());
        return authentication;
    }
    
    public User findByUsernameWithRentals(String username) {
        return userRepository.findByUsernameWithRentals(username);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
