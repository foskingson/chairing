package chairing.chairing.service.rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import chairing.chairing.domain.rental.Rental;
import chairing.chairing.domain.rental.RentalStatus;
import chairing.chairing.domain.user.User;
import chairing.chairing.service.user.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class RentalScheduler {

    private RentalService rentalService;
    private UserService userService;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void returnOverdueRentals() {
        List<User> users = getAllUsers(); // 모든 사용자 목록을 가져오는 메서드 필요

        LocalDate today = LocalDate.now(); // 현재 날짜를 가져옴

        for (User user : users) {
            Optional<Rental> activeRentalOpt = rentalService.findCurrentRentalByUser(user);
            if (activeRentalOpt.isPresent()) {
                Rental activeRental = activeRentalOpt.get();

                // 반납일과 현재 날짜 비교
                if (activeRental.getReturnDate() != null && activeRental.getReturnDate().isEqual(today)) {
                    activeRental.changeStatus(RentalStatus.RETURNED); // 상태를 RETURNED로 변경
                    rentalService.updateRental(activeRental); // 데이터베이스에 업데이트
                }
            }
        }
    }

    // 사용자 목록을 가져오는 메서드
    private List<User> getAllUsers() {
        return userService.findAllUsers(); // 사용자 목록을 가져오는 로직 필요
    }
}