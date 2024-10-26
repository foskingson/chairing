package chairing.chairing.controller.rental;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import chairing.chairing.config.JwtUtil;
import chairing.chairing.domain.rental.Rental;
import chairing.chairing.domain.user.User;
import chairing.chairing.domain.wheelchair.WheelchairType;
import chairing.chairing.dto.rent.RentalRequest;
import chairing.chairing.service.rental.RentalService;
import chairing.chairing.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping("/rental")
public class RentalController {

    
    private final RentalService rentalService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/checkRent")
    public ResponseEntity<Boolean> checkRent(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // "Bearer " 제거
        Long userId = jwtUtil.getUserIdFromToken(token);
        System.out.println(userId);
        User user = userService.getCurrentUser(userId);

        Optional<Rental> rent = rentalService.findCurrentRentalByUserV2(user);

        if (rent.isPresent()) {
            return ResponseEntity.ok(true); // 200 OK와 true 반환
        }else{
            return ResponseEntity.ok(false); // 200 OK와 false 반환
        }
    }
    
    
    @GetMapping("/currRent")
    public ResponseEntity<Rental> getCurrentUserRental(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // "Bearer " 제거
        Long userId = jwtUtil.getUserIdFromToken(token);
        System.out.println(userId);
        User user = userService.getCurrentUser(userId);

        Rental rental = rentalService.findCurrentRentalByUserV2(user).orElseThrow(() -> new IllegalArgumentException("현재 대여하고 있지 않습니다."));
        
        return ResponseEntity.ok(rental);
    }

    // 일반 사용자 페이지 환영 메시지
    @GetMapping
    public ResponseEntity<String> normalPage() {
        return ResponseEntity.ok("Welcome to the normal user page");
    }

    // 대여 폼 보여주기 (휠체어 종류 반환)
    @GetMapping("/rent")
    public ResponseEntity<List<WheelchairType>> showRentForm() {
        return ResponseEntity.ok(Arrays.asList(WheelchairType.values()));
    }

    // 휠체어 대여 처리
    @PostMapping("/rent")
    public ResponseEntity<Rental> rentWheelchair(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RentalRequest rentRequest // JSON 요청 본문을 처리
    ) {
        System.out.println("Authorization Header: " + authorizationHeader);
        System.out.println("Wheelchair Type: " + rentRequest.getWheelchairType());
        System.out.println("Return Date: " + rentRequest.getReturnDate());
        
        // 대여 시작일 출력
        LocalDate rentalDate = rentRequest.getRentalDate(); // 대여 시작일을 LocalDate로 가져옴
        System.out.println("Rental Date: " + rentalDate);

        // JWT에서 사용자 이름 추출
        String token = authorizationHeader.substring(7); // "Bearer " 제거
        String username = jwtUtil.extractUsername(token);

        // 반환 날짜와 대여 시작일을 LocalDate로 사용
        LocalDate returnDate = rentRequest.getReturnDate(); // 반납일

        // 휠체어 대여 서비스 호출
        Rental rental = rentalService.rentWheelchair(username, rentRequest.getWheelchairTypeEnum(), rentalDate, returnDate);

        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }
    


    // 휠체어 반납 처리
    @PostMapping("/return")
    public ResponseEntity<Rental> returnWheelchair(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7); // "Bearer " 제거
        String username;
    
        try {
            username = jwtUtil.extractUsername(token); // JWT에서 사용자 이름 추출
        } catch (Exception e) {
            // JWT 처리 중 오류 발생 시
            System.out.println("JWT 처리 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    
        try {
            Rental rental = rentalService.returnWheelchair(username);
            return ResponseEntity.ok(rental);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 대여 기록을 찾지 못했을 경우
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 이미 반납된 경우
        }
    }

        // 휠체어 대여 취소하기
        @PostMapping("/cancel")
        public ResponseEntity<Rental> cancelWheelchair(
                @RequestHeader("Authorization") String authorizationHeader
        ) {
            System.out.println("Cancel request received");
            String token = authorizationHeader.substring(7); // "Bearer " 제거
            String username;
        
            try {
                username = jwtUtil.extractUsername(token); // JWT에서 사용자 이름 추출
            } catch (Exception e) {
                // JWT 처리 중 오류 발생 시
                System.out.println("JWT 처리 중 오류 발생: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        
            try {
                Rental rental = rentalService.cancelWheelchair(username);
                return ResponseEntity.ok(rental);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 대여 기록을 찾지 못했을 경우
            } catch (IllegalStateException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 이미 반납된 경우
            }
        }


        @GetMapping("/guardian/{guardianCode}")
        public ResponseEntity<Rental> getRentalsByGuardianCode(@PathVariable String guardianCode) {
            Rental childRental = rentalService.findByRentalCode(guardianCode);
            return ResponseEntity.ok(childRental);
        }

        @GetMapping("/guardian/{guardianCode}/user")
        public ResponseEntity<User> getUserByGuardianCode(@PathVariable String guardianCode) {
            Rental childRental = rentalService.findByRentalCode(guardianCode);
            User childUser= childRental.getUser();
            return ResponseEntity.ok(childUser);
        }
}
