package chairing.chairing.controller.rental;

import chairing.chairing.config.JwtUtil;
import chairing.chairing.domain.rental.Rental;
import chairing.chairing.domain.rental.RentalStatus;
import chairing.chairing.domain.user.User;
import chairing.chairing.domain.wheelchair.Wheelchair;
import chairing.chairing.domain.wheelchair.WheelchairStatus;
import chairing.chairing.domain.wheelchair.WheelchairType;
import chairing.chairing.dto.rent.RentalRequest;
import chairing.chairing.repository.rental.RentalRepository;
import chairing.chairing.repository.user.UserRepository;
import chairing.chairing.repository.wheelchair.WheelchairRepository;
import chairing.chairing.service.rental.RentalService;
import chairing.chairing.service.user.UserService;
import chairing.chairing.service.wheelchair.WheelchairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rental")
public class RentalController {

    private final RentalService rentalService;
    private final WheelchairService wheelchairService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final WheelchairRepository wheelchairRepository;
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    // 대여 가능 여부 확인
    @GetMapping("/checkRent")
    public ResponseEntity<Boolean> checkRent(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); 
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getCurrentUser(userId);
        Optional<Rental> rent = rentalService.findCurrentRentalByUserV2(user);
        return ResponseEntity.ok(rent.isPresent());
    }

    // 현재 대여 정보 조회
    @GetMapping("/currRent")
    public ResponseEntity<Rental> getCurrentUserRental(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); 
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userService.getCurrentUser(userId);
        Rental rental = rentalService.findCurrentRentalByUserV2(user).orElseThrow(() -> new IllegalArgumentException("현재 대여 중인 정보가 없습니다."));
        return ResponseEntity.ok(rental);
    }

    // 대여 폼(휠체어 종류) 조회
    @GetMapping("/rent")
    public ResponseEntity<List<WheelchairType>> showRentForm() {
        return ResponseEntity.ok(Arrays.asList(WheelchairType.values()));
    }

    // 휠체어 대여 처리
    @PostMapping("/rent")
    public ResponseEntity<Rental> rentWheelchair(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody RentalRequest rentRequest
    ) {
        String token = authorizationHeader.substring(7); 
        String username = jwtUtil.extractUsername(token);
        LocalDate returnDate = rentRequest.getReturnDate();
        LocalDate rentalDate = rentRequest.getRentalDate();
        Rental rental = rentalService.rentWheelchair(username, rentRequest.getWheelchairTypeEnum(), rentalDate, returnDate);
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    // 대여 승인
    @PutMapping("/{rentalId}/approve")
    public ResponseEntity<Rental> approveRental(@PathVariable("rentalId") Long rentalId) {
        Rental rental = rentalService.approveRental(rentalId);
        return ResponseEntity.ok(rental);
    }

    // 대여 거절
    @PutMapping("/{rentalId}/reject")
    public ResponseEntity<Rental> rejectRental(@PathVariable("rentalId") Long rentalId) {
        Rental rental = rentalService.rejectRental(rentalId);
        return ResponseEntity.ok(rental);
    }

    // 휠체어 반납 처리
    @PutMapping("/return")
    @Transactional
    public ResponseEntity<Rental> returnWheelchair(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String username = jwtUtil.extractUsername(token);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Rental rental = rentalRepository.findCurrentRentalByUser(user).orElseThrow(() -> new IllegalArgumentException("대여 기록을 찾을 수 없습니다."));
        if (rental.getStatus() == RentalStatus.RETURNED) {
            throw new IllegalStateException("이미 반납된 대여입니다.");
        }
        Wheelchair wheelchair = rental.getWheelchair();
        wheelchair.changeStatus(WheelchairStatus.AVAILABLE);
        wheelchairRepository.save(wheelchair);
        rental.changeStatus(RentalStatus.RETURNED);
        return ResponseEntity.ok(rentalRepository.save(rental));
    }

    // 휠체어 대여 취소
    @PostMapping("/cancel")
    public ResponseEntity<Rental> cancelWheelchair(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); 
        String username = jwtUtil.extractUsername(token);
        Rental rental = rentalService.cancelWheelchair(username);
        return ResponseEntity.ok(rental);
    }

    // 보호자가 guardianCode를 사용해 대여 조회
    @GetMapping("/guardian/{guardianCode}")
    public ResponseEntity<Rental> getRentalsByGuardianCode(@PathVariable String guardianCode) {
        Rental childRental = rentalService.findByRentalCode(guardianCode);
        return ResponseEntity.ok(childRental);
    }

    // 보호자가 guardianCode로 대여 사용자의 정보 조회
    @GetMapping("/guardian/{guardianCode}/user")
    public ResponseEntity<User> getUserByGuardianCode(@PathVariable String guardianCode) {
        Rental childRental = rentalService.findByRentalCode(guardianCode);
        User childUser = childRental.getUser();
        return ResponseEntity.ok(childUser);
    }

    // 대기 중인 대여 목록 조회 메서드
    @GetMapping("/list")
    public ResponseEntity<List<Rental>> getWaitingRentals() {
        List<Rental> rentals = rentalService.getRentalsByStatus(RentalStatus.WAITING);
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/wheelchairs")
    public ResponseEntity<List<Wheelchair>> getWheelchairsByStatus(@RequestParam("status") String status) {
        List<Wheelchair> wheelchairs;
        if (status.equalsIgnoreCase("ALL")) {
            wheelchairs = wheelchairService.getAllWheelchairs();
        } else {
            WheelchairStatus wheelchairStatus = WheelchairStatus.valueOf(status.toUpperCase());
            wheelchairs = wheelchairService.findByStatus(wheelchairStatus); 
        }
        return ResponseEntity.ok(wheelchairs);
    }
}
