package chairing.chairing.service.rental;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import chairing.chairing.domain.delivery.Delivery;
import chairing.chairing.domain.delivery.DeliveryStatus;
import chairing.chairing.repository.delivery.DeliveryRepository;
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
    private final DeliveryRepository deliveryRepository;

    // 대여 코드 생성
    private String generateRentalCode() {
        return UUID.randomUUID().toString();
    }

    //대여
    @Transactional
    public Rental rentWheelchair(String username, WheelchairType wheelchairType, LocalDateTime returnDate) {
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
        Rental rental = new Rental(user, wheelchair, LocalDateTime.now(), returnDate, generateRentalCode(), RentalStatus.WAITING);

        // 휠체어 상태 변경
        wheelchair.changeStatus(WheelchairStatus.WAITING);
        wheelchairRepository.save(wheelchair);

        return rentalRepository.save(rental);
    }

    //반납
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

    //요청 승인
    public Rental approveRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental ID"));

        // 대여 상태를 ACTIVE로 변경
        rental.setStatus(RentalStatus.ACTIVE);
        rentalRepository.save(rental);

        // 휠체어 상태를 RENTED로 변경
        Wheelchair wheelchair = rental.getWheelchair();
        wheelchair.changeStatus(WheelchairStatus.RENTED);
        wheelchairRepository.save(wheelchair);  // 휠체어 상태 변경을 저장

        // 로그 추가
        System.out.println("Rental found: " + rental.getRentalId());

        // 기존에 동일한 rental로 등록된 delivery가 있는지 확인
        Optional<Delivery> existingDelivery = deliveryRepository.findByRental(rental);
        if (existingDelivery.isPresent()) {
            throw new IllegalStateException("A delivery for this rental already exists.");
        }

        // 새로운 Delivery 생성 및 설정
        Delivery delivery = new Delivery();
        delivery.setRental(rental); // 반드시 rental을 올바르게 설정
        System.out.println("Setting rental for delivery: " + rental.getRentalId());

        delivery.setTrackingNumber("12345");
        delivery.setDeliveryAddress(rental.getUser().getAddress());
        delivery.setName(rental.getUser().getUsername());
        delivery.setStatus(DeliveryStatus.PENDING);

        // Delivery 저장
        deliveryRepository.save(delivery);

        return rental;
    }
    
    //요청 거절
    @Transactional
    public Rental rejectRental(Long rentalId) {
        // rentalId를 사용하여 대여 요청을 조회
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental ID"));

        // 대여 요청의 상태를 REJECTED로 변경
        rental.setStatus(RentalStatus.REJECTED);
        rentalRepository.save(rental);

        // 로그 추가
        System.out.println("Rental rejected: " + rental.getRentalId());

        return rental;
    }

    //대여 취소
    @Transactional
    public Rental cancelWheelchair(String username) {
        // 로그인한 유저 정보 가져오기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 해당 사용자의 현재 대여 정보 가져오기
        Rental rental = rentalRepository.findCurrentRentalByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("대여 기록을 찾을 수 없습니다."));

        // 대여된 휠체어 상태를 "AVAILABLE"로 변경
        Wheelchair wheelchair = rental.getWheelchair();
        wheelchair.changeStatus(WheelchairStatus.AVAILABLE);
        wheelchairRepository.save(wheelchair);

        // 대여 상태를 "RETURNED"로 변경
        rental.changeStatus(RentalStatus.RETURNED);
        return rentalRepository.save(rental);
    }


    @Transactional(readOnly = true)
    public List<Rental> getRentalsByStatus(RentalStatus status) {
        return rentalRepository.findByStatus(status); // 상태를 기준으로 대여 목록을 조회
    }
}
