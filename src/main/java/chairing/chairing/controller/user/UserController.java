package chairing.chairing.controller.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import chairing.chairing.config.JwtUtil;
import chairing.chairing.domain.rental.Rental;
import chairing.chairing.domain.user.User;
import chairing.chairing.dto.guardian.GuardianProfileResponse;
import chairing.chairing.dto.user.LoginRequest;
import chairing.chairing.dto.user.UserCreateRequest;
import chairing.chairing.service.rental.RentalService;
import chairing.chairing.service.user.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;


import java.util.Collections;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final RentalService rentalService;
    private final JwtUtil jwtUtil;

    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid UserCreateRequest userCreateRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation errors occurred");
        }

        try {
            userService.create(userCreateRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Signup failed");
        }
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        return ResponseEntity.ok("Login page placeholder");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = userService.authenticate(loginRequest);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            String token = jwtUtil.generateToken(userDetails.getUsername());
    
            System.out.println("Login successful, returning token: " + token);  
            return ResponseEntity.ok(Collections.singletonMap("token", token));  
        } catch (Exception e) {
            System.out.println("Login failed due to: " + e.getMessage()); 
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No token provided, logout failed");
    }
        
    @GetMapping("/current")
    public User getCurrentUser(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtUtil.getUserIdFromToken(jwt);
        User user = userService.getCurrentUser(userId); 
        System.out.println(user.getRole());
        return user; 
    }

    @GetMapping("/guardian/current")
    public ResponseEntity<GuardianProfileResponse> getGuardianCurrentUser(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); 
        Long userId = jwtUtil.getUserIdFromToken(jwt); 
        User user = userService.getCurrentUser(userId); 

        Rental childRental = rentalService.findByRentalCode(user.getGuardianCode());
        User childUser= childRental.getUser();

        GuardianProfileResponse response = new GuardianProfileResponse(
            user.getUsername(),
            user.getPhoneNumber(),
            childUser.getUsername(),
            childUser.getPhoneNumber(),
            childRental.getRentalDate(),
            childRental.getReturnDate()
        );
        
        return ResponseEntity.ok(response); 
    }



    @GetMapping("/authStatus")
    public ResponseEntity<?> getAuthStatus(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            
            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    return ResponseEntity.ok("User authenticated: " + username);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT Token");
                }
            } catch (MalformedJwtException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Malformed JWT Token");
            } catch (ExpiredJwtException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Expired JWT Token");
            } catch (JwtException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT Token error");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header missing");
    }

}