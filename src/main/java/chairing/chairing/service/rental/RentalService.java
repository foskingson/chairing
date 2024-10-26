package chairing.chairing.service.rental;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import chairing.chairing.domain.rental.Rental;
import chairing.chairing.domain.rental.RentalStatus;
import chairing.chairing.domain.user.User;
import chairing.chairing.domain.wheelchair.Wheelchair;
import chairing.chairing.domain.wheelchair.WheelchairStatus;
import chairing.chairing.domain.wheelchair.WheelchairType;
import chairing.chairing.repository.rental.RentalRepository;
import chairing.chairing.repository.user.UserRepository;
import chairing.chairing.repository.wheelchair.WheelchairRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RentalService {
    
    private final RentalRepository rentalRepository;
    private final WheelchairRepository wheelchairRepository;
    private final UserRepository userRepository;

    // 대여 코드 생성
    private String generateRentalCode() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public Rental rentWheelchair(String username, WheelchairType wheelchairType, LocalDate rentalDate, LocalDate returnDate) {
        // 로그인한 유저 정보 가져오기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
            
        // 유저가 대여한 휠체어가 이미 있는 경우 대여를 막음
        List<Rental> currentRentals = rentalRepository.findByUser(user);
        if (!currentRentals.isEmpty()) {
            for (Rental currentRental : currentRentals) {
                if (currentRental.getStatus() == RentalStatus.ACTIVE) {
                    throw new IllegalStateException("현재 이미 휠체어를 빌리고 있습니다.");
                }
            }
        }
    
        // 선택한 타입의 대여 가능한 휠체어 중 하나 가져오기
        Wheelchair wheelchair = wheelchairRepository.findFirstByTypeAndStatus(wheelchairType, WheelchairStatus.AVAILABLE)
                .orElseThrow(() -> new IllegalArgumentException("대여 가능한 휠체어가 없습니다."));
    
        // 대여 정보 생성 및 저장
        Rental rental = new Rental(user, wheelchair, rentalDate, returnDate, generateRentalCode(), RentalStatus.WAITING); // rentalDate로 수정
    
        // 휠체어 상태 변경
        wheelchair.changeStatus(WheelchairStatus.WAITING);
        wheelchairRepository.save(wheelchair);
    
        return rentalRepository.save(rental);
    }
    
    
    @Transactional
    public Rental returnWheelchair(String username) {
        // 로그인한 유저 정보 가져오기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        
        // 해당 사용자의 현재 대여 정보 가져오기
        Rental rental = rentalRepository.findCurrentRentalByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("대여 기록을 찾을 수 없습니다."));

        // 이미 반납된 경우 예외 처리
        if (rental.getStatus() == RentalStatus.RETURNED) {
            throw new IllegalStateException("이미 반납된 대여입니다.");
        }

        // 대여된 휠체어 상태를 "AVAILABLE"로 변경
        Wheelchair wheelchair = rental.getWheelchair();
        wheelchair.changeStatus(WheelchairStatus.AVAILABLE);
        wheelchairRepository.save(wheelchair);

        // 대여 상태를 "RETURNED"로 변경
        rental.changeStatus(RentalStatus.RETURNED);
        return rentalRepository.save(rental);
    }

    @Transactional
    public Optional<Rental> findCurrentRentalByUser(User user) {
        return rentalRepository.findCurrentRentalByUser(user);
    }

    @Transactional
    public Optional<Rental> findCurrentRentalByUserV2(User user) {
        return rentalRepository.findCurrentRentalByUserV2(user);
    }

    @Transactional
    public void updateRental(Rental rental) {
        rentalRepository.save(rental);
    }

    @Transactional
    public Rental cancelWheelchair(String username) {
        // 로그인한 유저 정보 가져오기
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        
        // 해당 사용자의 현재 대여 정보 가져오기
        Rental rental = rentalRepository.findCurrentRentalByUserV2(user)
                .orElseThrow(() -> new IllegalArgumentException("대여 기록을 찾을 수 없습니다."));

        // 대여된 휠체어 상태를 "AVAILABLE"로 변경
        Wheelchair wheelchair = rental.getWheelchair();
        wheelchair.changeStatus(WheelchairStatus.AVAILABLE);
        wheelchairRepository.save(wheelchair);
        rentalRepository.delete(rental);
        return rental;
    }

    public Rental findByRentalCode(String guardianCode) {
        return rentalRepository.findByRentalCode(guardianCode).orElseThrow(() -> new IllegalArgumentException("대여 기록을 찾을 수 없습니다."));
    }
}
