package chairing.chairing.service.rental;

import java.time.LocalDate;
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

    //대여
    @Transactional
    public Rental rentWheelchair(String username, WheelchairType wheelchairType, LocalDate rentalDate, LocalDate returnDate) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        List<Rental> currentRentals = rentalRepository.findByUser(user);
        if (!currentRentals.isEmpty()) {
            for (Rental currentRental : currentRentals) {
                if (currentRental.getStatus() == RentalStatus.ACTIVE) {
                    throw new IllegalStateException("현재 이미 휠체어를 빌리고 있습니다.");
                }
            }
        }

        Wheelchair wheelchair = wheelchairRepository.findFirstByTypeAndStatus(wheelchairType, WheelchairStatus.AVAILABLE)
                .orElseThrow(() -> new IllegalArgumentException("대여 가능한 휠체어가 없습니다."));

        Rental rental = new Rental(user, wheelchair, rentalDate, returnDate, generateRentalCode(), RentalStatus.WAITING); 
    

        wheelchair.changeStatus(WheelchairStatus.WAITING);
        wheelchairRepository.save(wheelchair);

        return rentalRepository.save(rental);
    }

    //반납
    @Transactional
    public Rental returnWheelchair(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Rental rental = rentalRepository.findCurrentRentalByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("대여 기록을 찾을 수 없습니다."));


        if (rental.getStatus() == RentalStatus.RETURNED) {
            throw new IllegalStateException("이미 반납된 대여입니다.");
        }

        Wheelchair wheelchair = rental.getWheelchair();
        wheelchair.changeStatus(WheelchairStatus.AVAILABLE);
        wheelchairRepository.save(wheelchair);

        rental.changeStatus(RentalStatus.RETURNED);
        return rentalRepository.save(rental);
    }

    //요청 승인
    @Transactional
    public Rental approveRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental ID"));

        rental.setStatus(RentalStatus.ACTIVE);
        rentalRepository.save(rental);

        Wheelchair wheelchair = rental.getWheelchair();
        wheelchair.changeStatus(WheelchairStatus.RENTED);
        wheelchairRepository.save(wheelchair); 

        System.out.println("Rental found: " + rental.getRentalId());

        System.out.println("Setting rental for delivery: " + rental.getRentalId());

        return rental;
    }
    
    //요청 거절
    @Transactional
    public Rental rejectRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental ID"));

        Wheelchair wheelchair = rental.getWheelchair();
        wheelchair.changeStatus(WheelchairStatus.AVAILABLE);
        wheelchairRepository.save(wheelchair);

        rentalRepository.delete(rental);

        System.out.println("Rental rejected: " + rental.getRentalId());

        return rental;
    }

    //대여 취소
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
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        
        Rental rental = rentalRepository.findCurrentRentalByUserV2(user)
                .orElseThrow(() -> new IllegalArgumentException("대여 기록을 찾을 수 없습니다."));

        Wheelchair wheelchair = rental.getWheelchair();
        wheelchair.changeStatus(WheelchairStatus.AVAILABLE);
        wheelchairRepository.save(wheelchair);
        rentalRepository.delete(rental);
        return rental;
    }

    public Rental findByRentalCode(String guardianCode) {
        return rentalRepository.findByRentalCode(guardianCode).orElseThrow(() -> new IllegalArgumentException("대여 기록을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<Rental> getRentalsByStatus(RentalStatus status) {
        return rentalRepository.findByStatus(status); 
    }
}