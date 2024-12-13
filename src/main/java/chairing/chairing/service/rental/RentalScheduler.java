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

    @Scheduled(cron = "0 0 0 * * ?") 
    public void returnOverdueRentals() {
        List<User> users = getAllUsers(); 

        LocalDate today = LocalDate.now();

        for (User user : users) {
            Optional<Rental> activeRentalOpt = rentalService.findCurrentRentalByUser(user);
            if (activeRentalOpt.isPresent()) {
                Rental activeRental = activeRentalOpt.get();

                if (activeRental.getReturnDate() != null && activeRental.getReturnDate().isEqual(today)) {
                    activeRental.changeStatus(RentalStatus.RETURNED); 
                    rentalService.updateRental(activeRental); 
                }
            }
        }
    }

    private List<User> getAllUsers() {
        return userService.findAllUsers(); 
    }
}