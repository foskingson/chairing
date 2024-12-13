package chairing.chairing.domain.rental;

import java.time.LocalDate;

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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "rentalId") 
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "wheelchair_id")
    private Wheelchair wheelchair; 

    @Column(nullable = false)
    private LocalDate rentalDate;

    private LocalDate returnDate;

    @Column(nullable = false)
    private String rentalCode; 

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;

    public void changeStatus(RentalStatus newStatus){
        status = newStatus;
    }

    public Rental(User user, Wheelchair wheelchair, LocalDate rentalDate, LocalDate returnDate,
            String rentalCode, RentalStatus status) {
        this.user = user;
        this.wheelchair = wheelchair;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.rentalCode = rentalCode;
        this.status = status;
    }
}
