package chairing.chairing.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import chairing.chairing.domain.user.User;
import chairing.chairing.repository.user.UserRepository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    
    private SecretKey key;

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateToken(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole());
        claims.put("phoneNumber", user.getPhoneNumber());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) 
                .signWith(key, SignatureAlgorithm.HS256) 
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }
    

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);  
            return true;
        } catch (Exception e) {
            return false; 
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public User getUserFromToken(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) 
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long userId = claims.get("userId", Long.class);
        String username = claims.getSubject();
        
        User user = new User();
        user.setUserId(userId);
        user.setUsername(username);

        return user;
    }

}
