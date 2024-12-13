package chairing.chairing.service.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import chairing.chairing.domain.user.UserRole;
import chairing.chairing.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Trying to authenticate user: " + username); 
        chairing.chairing.domain.user.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user.getRole().equals(UserRole.ADMIN)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.name()));
        } else if (user.getRole().equals(UserRole.NORMAL)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.NORMAL.name()));
        } else if (user.getRole().equals(UserRole.GUARDIAN)) {
            authorities.add(new SimpleGrantedAuthority(UserRole.GUARDIAN.name()));
        }

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
