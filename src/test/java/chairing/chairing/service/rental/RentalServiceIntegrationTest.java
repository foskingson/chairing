package chairing.chairing.service.rental;
import chairing.chairing.domain.rental.Rental;
import chairing.chairing.domain.rental.RentalStatus;
import chairing.chairing.domain.user.User;
import chairing.chairing.domain.user.UserRole;
import chairing.chairing.domain.wheelchair.Location;
import chairing.chairing.domain.wheelchair.Wheelchair;
import chairing.chairing.domain.wheelchair.WheelchairStatus;
import chairing.chairing.domain.wheelchair.WheelchairType;
import chairing.chairing.repository.rental.RentalRepository;
import chairing.chairing.repository.user.UserRepository;
import chairing.chairing.repository.wheelchair.WheelchairRepository;
import chairing.chairing.service.rental.RentalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 테스트 후 롤백을 위해 사용
public class RentalServiceIntegrationTest {

    @Autowired
    private RentalRepository rentalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WheelchairRepository wheelchairRepository;

    private RentalService rentalService;


    @BeforeEach
    void setUp() {
        userRepository.save(new User("testuser", "123", "123", UserRole.NORMAL, null));

        // 서비스 객체에 실제 리포지토리 주입
        rentalService = new RentalService(rentalRepository, wheelchairRepository, userRepository);

        // 유아용 및 성인용 휠체어 각각 100개씩 생성하여 저장
        for (int i = 0; i < 100; i++) {
            // 유아용 휠체어 생성
            Wheelchair childWheelchair = new Wheelchair(WheelchairStatus.AVAILABLE, WheelchairType.CHILD, new Location(0, 0));
            wheelchairRepository.save(childWheelchair);

            // 성인용 휠체어 생성
            Wheelchair adultWheelchair = new Wheelchair(WheelchairStatus.AVAILABLE, WheelchairType.ADULT, new Location(0, 0));
            wheelchairRepository.save(adultWheelchair);
        }
    }

    // @Test
    // void 휠체어_대여_테스트() {
    //     // Principal 객체 모킹
    //     Principal principal = Mockito.mock(Principal.class);
    //     Mockito.when(principal.getName()).thenReturn("testuser");

    //     // 휠체어 대여 시도
    //     LocalDateTime returnDate = LocalDateTime.now().plusDays(7);
    //     Rental rental = rentalService.rentWheelchair(principal, WheelchairType.ADULT, returnDate);

    //     // 휠체어가 대여된 상태인지 확인
    //     assertEquals(RentalStatus.ACTIVE, rental.getStatus());
    //     assertEquals(WheelchairStatus.RENTED, rental.getWheelchair().getStatus());
    //     assertNotNull(rental.getRentalCode());
    // }

    // @Test
    // void 휠체어_반납_테스트() {
    //     // Principal 객체 모킹
    //     Principal principal = Mockito.mock(Principal.class);
    //     Mockito.when(principal.getName()).thenReturn("testuser");

    //     // 먼저 대여
    //     LocalDateTime returnDate = LocalDateTime.now().plusDays(7);
    //     Rental rental = rentalService.rentWheelchair(principal, WheelchairType.ADULT, returnDate);

    //     // 반납 시도
    //     Rental returnedRental = rentalService.returnWheelchair(principal, rental.getRentalId());

    //     // 반납된 상태인지 확인
    //     assertEquals(RentalStatus.RETURNED, returnedRental.getStatus());
    //     assertEquals(WheelchairStatus.AVAILABLE, returnedRental.getWheelchair().getStatus());
    // }
}
