package chairing.chairing.dto.guardian;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GuardianProfileResponse {
    private String username;
    private String phoneNumber;
    private String childUsername;
    private String childPhoneNumber;
    private LocalDate rentalDate;
    private LocalDate returnDate;
}
