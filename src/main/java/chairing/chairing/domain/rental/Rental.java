package chairing.chairing.domain.rental;

import java.time.LocalDate;
import java.time.LocalDateTime;

import chairing.chairing.domain.user.User;
import chairing.chairing.domain.wheelchair.Wheelchair;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "rentalId") // Identity-based reference handling
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // No @JsonBackReference needed

    @ManyToOne
    @JoinColumn(name = "wheelchair_id")
    private Wheelchair wheelchair;  // You can also handle this with @JsonIdentityInfo

    @Column(nullable = false)
    private LocalDate rentalDate;

    private LocalDate returnDate;

    @Column(nullable = false)
    private String rentalCode; // Arbitrary code

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    public void changeStatus(RentalStatus newStatus){
        status = newStatus;
    }

<<<<<<< HEAD
    public Rental(User user, Wheelchair wheelchair, LocalDateTime rentalDate, LocalDateTime returnDate,
                  String rentalCode, RentalStatus status) {
=======
    public Rental(User user, Wheelchair wheelchair, LocalDate rentalDate, LocalDate returnDate,
            String rentalCode, RentalStatus status) {
>>>>>>> 53cce2cea0a5fb93212811c19d8c353bd1f4a7c6
        this.user = user;
        this.wheelchair = wheelchair;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.rentalCode = rentalCode;
        this.status = status;
    }
}
